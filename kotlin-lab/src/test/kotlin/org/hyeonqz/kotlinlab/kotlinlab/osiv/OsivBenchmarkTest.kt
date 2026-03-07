package org.hyeonqz.kotlinlab.kotlinlab.osiv

import com.zaxxer.hikari.HikariDataSource
import org.hyeonqz.kotlinlab.kotlinlab.osiv.application.OrderService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.sql.DataSource
import kotlin.system.measureTimeMillis

@SpringBootTest
class OsivBenchmarkTest {

    @Autowired lateinit var orderService: OrderService
    @Autowired lateinit var dataSource: DataSource

    private val testOrderId = 1L

    //  벤치마킹 테스트 전략
    //  1. SpringBootTest 통합 테스트 — 커넥션 점유 시간 측정
    //  2. JMH 마이크로 벤치마크 — TPS / 응답시간 측정
    //  3. HikariCP 메트릭 수집

    //  테스트 1 — 단건 응답시간 측정 (10,000회 반복)
    @Test
    @DisplayName("단건 응답시간 비교 — RAW(Lazy) vs DTO(FetchJoin)")
    fun singleRequestLatencyTest() {
        val warmup = 1000
        val iteration = 10_000

        // Warm-up
        repeat(warmup) { orderService.findOrderDto(testOrderId) }

        // RAW 측정
        val rawTimes = LongArray(iteration)
        repeat(iteration) { i ->
            rawTimes[i] = measureTimeMillis { orderService.findOrderRaw(testOrderId) }
        }

        // DTO 측정
        val dtoTimes = LongArray(iteration)
        repeat(iteration) { i ->
            dtoTimes[i] = measureTimeMillis { orderService.findOrderDto(testOrderId) }
        }

        printLatencyResult("RAW (Lazy — OSIV 의존)", rawTimes)
        printLatencyResult("DTO (FetchJoin — OSIV 독립)", dtoTimes)
    }

    // ──────────────────────────────────────────────────────────
    //  테스트 2 — 동시 부하 테스트 (커넥션 풀 고갈 시뮬레이션)
    // ──────────────────────────────────────────────────────────
    @Test
    @DisplayName("동시 부하 — 커넥션 풀 고갈 시뮬레이션 (OSIV true 위험 재현)")
    fun concurrentLoadTest() {
        val concurrency = 20      // 동시 스레드 수 (풀 사이즈보다 크게 설정)
        val requestsPerThread = 50
        val executor = Executors.newFixedThreadPool(concurrency)

        println("\n=== 동시 부하 테스트 시작 (동시 $concurrency 스레드) ===")
        printHikariStats("테스트 시작 전")

        // ── OSIV=true 시나리오 시뮬레이션 ────────────────────
        //    커넥션을 오래 잡고 있는 상황을 인위적으로 만듦
        val osivTrueSuccess  = AtomicInteger(0)
        val osivTrueFailure  = AtomicInteger(0)
        val osivTrueTotalMs  = AtomicLong(0)

        val latch1 = CountDownLatch(concurrency)
        val futures1 = (1..concurrency).map {
            executor.submit {
                repeat(requestsPerThread) {
                    try {
                        val elapsed = measureTimeMillis {
                            val order = orderService.findOrderRaw(testOrderId)
                            // OSIV=true 시뮬레이션: 커넥션 점유 상태에서 추가 작업
                            simulateViewRendering(150)  // 150ms JSON 직렬화 + 네트워크 지연 시뮬레이션
                            // Lazy 접근 (OSIV=true 환경에서만 정상)
                            order.merchant.name
                            order.items.size
                        }
                        osivTrueTotalMs.addAndGet(elapsed)
                        osivTrueSuccess.incrementAndGet()
                    } catch (e: Exception) {
                        osivTrueFailure.incrementAndGet()
                    }
                }
                latch1.countDown()
            }
        }
        latch1.await(60, TimeUnit.SECONDS)
        printHikariStats("OSIV=true 시나리오 후")

        // ── OSIV=false 시나리오 (DTO 패턴) ───────────────────
        val osivFalseSuccess = AtomicInteger(0)
        val osivFalseFailure = AtomicInteger(0)
        val osivFalseTotalMs = AtomicLong(0)

        val latch2 = CountDownLatch(concurrency)
        (1..concurrency).map {
            executor.submit {
                repeat(requestsPerThread) {
                    try {
                        val elapsed = measureTimeMillis {
                            orderService.findOrderDto(testOrderId)
                            // 커넥션 반납 후 View 작업
                            simulateViewRendering(150)
                        }
                        osivFalseTotalMs.addAndGet(elapsed)
                        osivFalseSuccess.incrementAndGet()
                    } catch (e: Exception) {
                        osivFalseFailure.incrementAndGet()
                    }
                }
                latch2.countDown()
            }
        }
        latch2.await(60, TimeUnit.SECONDS)
        printHikariStats("OSIV=false 시나리오 후")

        val totalRequests = concurrency * requestsPerThread
        println("\n╔══════════════════════════════════════════════════════╗")
        println("║            동시 부하 테스트 결과 비교                 ║")
        println("╠══════════════════════════════════════════════════════╣")
        println("║  구분              OSIV=true      OSIV=false         ║")
        println("╠══════════════════════════════════════════════════════╣")
        println("║  성공              ${pad(osivTrueSuccess.get())}          ${pad(osivFalseSuccess.get())}         ║")
        println("║  실패              ${pad(osivTrueFailure.get())}          ${pad(osivFalseFailure.get())}         ║")
        println("║  평균 응답(ms)     ${pad(osivTrueTotalMs.get() / maxOf(osivTrueSuccess.get(), 1))}         ${pad(osivFalseTotalMs.get() / maxOf(osivFalseSuccess.get(), 1))}        ║")
        println("╚══════════════════════════════════════════════════════╝")

        executor.shutdown()
    }

    //  테스트 3 — 커넥션 점유 시간 직접 측정
    @Test
    @DisplayName("커넥션 점유 시간 직접 측정 — HikariCP 메트릭 기반")
    fun connectionHoldTimeTest() {
        val hikari = dataSource as HikariDataSource
        val pool   = hikari.hikariPoolMXBean

        println("\n=== 커넥션 점유 시간 측정 ===")
        println("초기 상태 — 유휴: ${pool.idleConnections}, 활성: ${pool.activeConnections}")

        // ──────────────────────────────────────────────────────
        //  RAW 시나리오 — OSIV=true 시뮬레이션
        //  트랜잭션 종료 후 Lazy 접근을 직접 하지 않고
        //  "커넥션 점유 시간"만 측정
        // ──────────────────────────────────────────────────────
        val rawStart = System.currentTimeMillis()
        val order = orderService.findOrderRaw(testOrderId)
        val rawServiceEnd = System.currentTimeMillis()

        // Lazy 필드를 직접 접근하지 않고 프록시 상태만 확인
        // OSIV=true 라면 여기서 정상 접근 가능
        // OSIV=false 라면 이 블록이 실패함 → 예외 캐치로 상태 기록
        var lazyAccessResult = "접근 불가 (OSIV=false — 예상된 동작)"
        try {
            val merchantName = order.merchant.name  // Lazy 접근 시도
            val itemCount    = order.items.size
            lazyAccessResult = "접근 성공 (OSIV=true) — merchant=$merchantName, items=$itemCount"
        } catch (e: org.hibernate.LazyInitializationException) {
            // OSIV=false 일 때 예상되는 정상 실패
            lazyAccessResult = "LazyInitializationException 발생 (OSIV=false — 예상된 동작) ✅"
        }

        simulateViewRendering(100)
        val rawTotalEnd = System.currentTimeMillis()

        println("\n[RAW — OSIV 의존 시나리오]")
        println("  Service 처리 시간 : ${rawServiceEnd - rawStart}ms")
        println("  View 포함 총 시간 : ${rawTotalEnd - rawStart}ms")
        println("  Lazy 접근 결과    : $lazyAccessResult")

        //  DTO 시나리오 — OSIV=true/false 모두 안전
        //  트랜잭션 안에서 DTO 변환 완료 → 세션 불필요
        val dtoStart = System.currentTimeMillis()
        val dto = orderService.findOrderDto(testOrderId)
        val dtoServiceEnd = System.currentTimeMillis()

        // 커넥션 반납 후 View 작업 — 세션 없어도 정상
        simulateViewRendering(100)
        val dtoTotalEnd = System.currentTimeMillis()

        println("\n[DTO — OSIV 독립 시나리오]")
        println("  Service 처리 시간 : ${dtoServiceEnd - dtoStart}ms  ← 커넥션 점유 구간")
        println("  View 포함 총 시간 : ${dtoTotalEnd - dtoStart}ms")
        println("  DTO 접근 결과     : merchant=${dto.merchantName}, items=${dto.itemCount} ✅")

        // ──────────────────────────────────────────────────────
        //  결과 요약
        // ──────────────────────────────────────────────────────
        println("\n╔══════════════════════════════════════════════════════╗")
        println("║          커넥션 점유 시간 측정 결과                   ║")
        println("╠══════════════════════════════════════════════════════╣")
        println("║  RAW  Service 처리  : ${pad(rawServiceEnd - rawStart)}ms                  ║")
        println("║  RAW  View 포함     : ${pad(rawTotalEnd - rawStart)}ms  ← OSIV=true 점유  ║")
        println("║  DTO  Service 처리  : ${pad(dtoServiceEnd - dtoStart)}ms  ← 실제 점유 구간║")
        println("║  DTO  View 포함     : ${pad(dtoTotalEnd - dtoStart)}ms                   ║")
        println("╠══════════════════════════════════════════════════════╣")
        println("║  절감 가능 시간     : ${pad((rawTotalEnd - rawStart) - (dtoServiceEnd - dtoStart))}ms                  ║")
        println("╚══════════════════════════════════════════════════════╝")

        printHikariStats("테스트 완료 후")
    }

    private fun simulateViewRendering(ms: Long) = Thread.sleep(ms)

    private fun printHikariStats(label: String) {
        val hikari = dataSource as HikariDataSource
        val pool   = hikari.hikariPoolMXBean
        println("[$label] 총: ${pool.totalConnections}, 활성: ${pool.activeConnections}, 유휴: ${pool.idleConnections}, 대기: ${pool.threadsAwaitingConnection}")
    }

    private fun printLatencyResult(label: String, times: LongArray) {
        times.sort()
        println("\n[$label]")
        println("  avg  : ${times.average().toLong()}ms")
        println("  p50  : ${times[times.size / 2]}ms")
        println("  p95  : ${times[(times.size * 0.95).toInt()]}ms")
        println("  p99  : ${times[(times.size * 0.99).toInt()]}ms")
        println("  max  : ${times.last()}ms")
    }

    private fun pad(n: Int) = n.toString().padEnd(8)
    private fun pad(n: Long) = n.toString().padEnd(8)
}