# Virtual Thread 실습 - 은행 이체 처리 시스템

## 시나리오 설명

은행에서 **대량 이체 요청**이 동시에 들어오는 상황입니다.

- 하루 평균 **10만 건**의 이체 요청
- 각 이체는 다음 순서로 처리됨:
  1. 출금 계좌 잔액 확인 (DB 조회 - 50ms)
  2. 외부 은행 API 호출 (네트워크 - 100ms)
  3. 이체 결과 저장 (DB 쓰기 - 30ms)
- 총 처리 시간: 건당 약 **180ms** (대부분 I/O 대기)

**문제**: 플랫폼 스레드(200개)로는 동시 처리량이 한계에 부딪힘

---

## 프로젝트 구조

```
src/
└── main/java/bank/
    ├── model/
    │   ├── Account.java          # 계좌 모델
    │   └── TransferRequest.java  # 이체 요청 모델
    ├── service/
    │   ├── AccountRepository.java   # DB 조회 시뮬레이션
    │   ├── ExternalBankApi.java     # 외부 API 시뮬레이션
    │   └── TransferService.java     # 이체 처리 핵심 로직
    ├── step1/
    │   └── PlatformThreadTransfer.java  # 플랫폼 스레드 구현
    ├── step2/
    │   └── VirtualThreadTransfer.java   # Virtual Thread 구현
    ├── step3/
    │   └── PinningProblemDemo.java      # Pinning 문제 시연
    └── BenchmarkRunner.java             # 전체 비교 실행
```

---

## Step 0: 공통 모델 및 시뮬레이션 클래스

### Account.java
```java
package bank.model;

public class Account {
    private final String accountId;
    private volatile long balance;

    public Account(String accountId, long balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public String getAccountId() { return accountId; }
    public long getBalance() { return balance; }
    public void setBalance(long balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "Account{id=" + accountId + ", balance=" + balance + "}";
    }
}
```

### TransferRequest.java
```java
package bank.model;

public record TransferRequest(
    String transferId,
    String fromAccountId,
    String toAccountId,
    long amount
) {}
```

### AccountRepository.java - DB I/O 시뮬레이션
```java
package bank.service;

import bank.model.Account;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepository {

    // 인메모리 DB 시뮬레이션
    private static final Map<String, Account> DB = new ConcurrentHashMap<>();

    static {
        // 테스트용 계좌 1000개 초기화
        for (int i = 0; i < 1000; i++) {
            String id = "ACC" + String.format("%04d", i);
            DB.put(id, new Account(id, 1_000_000L));
        }
    }

    // DB 조회 시뮬레이션 (50ms 지연)
    public Account findById(String accountId) throws InterruptedException {
        Thread.sleep(50); // SELECT 쿼리 지연
        Account account = DB.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("계좌 없음: " + accountId);
        }
        return account;
    }

    // DB 저장 시뮬레이션 (30ms 지연)
    public void save(Account account) throws InterruptedException {
        Thread.sleep(30); // UPDATE 쿼리 지연
        DB.put(account.getAccountId(), account);
    }
}
```

### ExternalBankApi.java - 외부 API 시뮬레이션
```java
package bank.service;

public class ExternalBankApi {

    // 외부 은행 API 호출 시뮬레이션 (100ms 지연)
    public boolean requestTransfer(String fromId, String toId, long amount)
            throws InterruptedException {
        Thread.sleep(100); // 네트워크 I/O 지연
        // 실패 시뮬레이션: 0.1% 확률로 실패
        return Math.random() > 0.001;
    }
}
```

### TransferService.java - 핵심 이체 로직
```java
package bank.service;

import bank.model.Account;
import bank.model.TransferRequest;

public class TransferService {

    private final AccountRepository repository;
    private final ExternalBankApi externalApi;

    public TransferService() {
        this.repository = new AccountRepository();
        this.externalApi = new ExternalBankApi();
    }

    /**
     * 이체 처리 (총 ~180ms 소요 - 대부분 I/O 대기)
     * 1. 출금 계좌 조회 (50ms)
     * 2. 외부 API 호출 (100ms)
     * 3. 결과 저장 (30ms)
     */
    public boolean transfer(TransferRequest request) throws InterruptedException {
        // 1. 출금 계좌 잔액 확인 (DB I/O)
        Account fromAccount = repository.findById(request.fromAccountId());
        if (fromAccount.getBalance() < request.amount()) {
            System.err.println("[FAIL] 잔액 부족: " + request.transferId());
            return false;
        }

        // 2. 외부 은행 API 호출 (Network I/O)
        boolean success = externalApi.requestTransfer(
            request.fromAccountId(),
            request.toAccountId(),
            request.amount()
        );

        if (!success) {
            System.err.println("[FAIL] 외부 API 실패: " + request.transferId());
            return false;
        }

        // 3. 이체 결과 저장 (DB I/O)
        fromAccount.setBalance(fromAccount.getBalance() - request.amount());
        repository.save(fromAccount);

        return true;
    }
}
```

---

## Step 1: 플랫폼 스레드 - 문제 상황

```java
package bank.step1;

import bank.model.TransferRequest;
import bank.service.TransferService;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class PlatformThreadTransfer {

    private static final int THREAD_POOL_SIZE = 200;  // 플랫폼 스레드 200개
    private static final int TRANSFER_COUNT = 1_000;   // 이체 요청 1000건

    public static void main(String[] args) throws Exception {
        System.out.println("=== [Step 1] 플랫폼 스레드 ===");
        System.out.println("스레드 수: " + THREAD_POOL_SIZE);
        System.out.println("이체 요청 수: " + TRANSFER_COUNT);
        System.out.println();

        List<TransferRequest> requests = generateRequests(TRANSFER_COUNT);
        TransferService service = new TransferService();

        // 플랫폼 스레드 풀 생성
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(TRANSFER_COUNT);

        long startTime = System.currentTimeMillis();

        for (TransferRequest request : requests) {
            executor.submit(() -> {
                try {
                    boolean result = service.transfer(request);
                    if (result) successCount.incrementAndGet();
                    else failCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long elapsed = System.currentTimeMillis() - startTime;

        executor.shutdown();

        // 결과 출력
        System.out.println("=== 결과 ===");
        System.out.println("총 소요 시간: " + elapsed + "ms");
        System.out.println("성공: " + successCount.get() + "건");
        System.out.println("실패: " + failCount.get() + "건");
        System.out.println("처리량: " + (TRANSFER_COUNT * 1000L / elapsed) + "건/초");
        System.out.println();

        // 이론적 최소 시간 계산
        // - 각 요청 180ms
        // - 스레드 200개로 1000건 처리
        // - 이론상 최소: ceil(1000/200) * 180ms = 5 * 180ms = 900ms
        System.out.println("[분석] 플랫폼 스레드 " + THREAD_POOL_SIZE + "개로 " + TRANSFER_COUNT + "건 처리");
        System.out.println("[분석] 대부분의 시간을 I/O 대기로 낭비");
        System.out.println("[분석] 스레드 수를 늘리면 메모리 부족 위험 (스레드당 ~1MB)");
    }

    static List<TransferRequest> generateRequests(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new TransferRequest(
                "TXN" + String.format("%06d", i),
                "ACC" + String.format("%04d", i % 500),
                "ACC" + String.format("%04d", (i + 500) % 1000),
                1000L
            ))
            .toList();
    }
}
```

**실행 결과 예시:**
```
총 소요 시간: ~900ms ~ 2000ms
처리량: ~500 ~ 1100건/초
문제: 스레드 200개가 모두 I/O 대기 중 → CPU 활용률 낮음
```

---

## Step 2: Virtual Thread - 문제 해결

```java
package bank.step2;

import bank.model.TransferRequest;
import bank.service.TransferService;
import bank.step1.PlatformThreadTransfer;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadTransfer {

    private static final int TRANSFER_COUNT = 1_000;

    public static void main(String[] args) throws Exception {
        System.out.println("=== [Step 2] Virtual Thread ===");
        System.out.println("Virtual Thread: 요청마다 새 스레드 생성 (비용 거의 없음)");
        System.out.println("이체 요청 수: " + TRANSFER_COUNT);
        System.out.println();

        List<TransferRequest> requests = PlatformThreadTransfer.generateRequests(TRANSFER_COUNT);
        TransferService service = new TransferService();

        // Virtual Thread Executor - 요청마다 Virtual Thread 생성
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(TRANSFER_COUNT);

        long startTime = System.currentTimeMillis();

        for (TransferRequest request : requests) {
            executor.submit(() -> {
                try {
                    boolean result = service.transfer(request);
                    if (result) successCount.incrementAndGet();
                    else failCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long elapsed = System.currentTimeMillis() - startTime;

        executor.shutdown();

        System.out.println("=== 결과 ===");
        System.out.println("총 소요 시간: " + elapsed + "ms");
        System.out.println("성공: " + successCount.get() + "건");
        System.out.println("실패: " + failCount.get() + "건");
        System.out.println("처리량: " + (TRANSFER_COUNT * 1000L / elapsed) + "건/초");
        System.out.println();
        System.out.println("[분석] Virtual Thread는 I/O 대기 시 Carrier Thread 반환");
        System.out.println("[분석] 소수의 Carrier Thread로 수천 개 Virtual Thread 처리");
        System.out.println("[분석] 메모리 사용량 대폭 감소 (Virtual Thread당 수KB)");
    }
}
```

**실행 결과 예시:**
```
총 소요 시간: ~180ms ~ 250ms  (플랫폼 스레드 대비 4~10배 빠름)
처리량: ~4000 ~ 5000건/초
이유: 1000개 Virtual Thread가 동시 실행, I/O 대기 시 Carrier Thread 반환
```

---

## Step 3: Pinning 문제 - 흔한 실수

```java
package bank.step3;

import bank.model.TransferRequest;
import bank.service.AccountRepository;
import bank.step1.PlatformThreadTransfer;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Virtual Thread의 Pinning 문제 시연
 *
 * JVM 옵션 추가: -Djdk.tracePinnedThreads=full
 * → Pinning 발생 시 스택 트레이스 출력됨
 */
public class PinningProblemDemo {

    private static final AccountRepository repository = new AccountRepository();

    // 문제 있는 버전: synchronized 사용
    static class BadTransferService {
        private final Object lock = new Object();

        public void transfer(TransferRequest request) throws InterruptedException {
            synchronized (lock) {                    // Pinning 발생!
                repository.findById(request.fromAccountId()); // I/O 대기 중 Carrier Thread 점유
                repository.save(repository.findById(request.toAccountId()));
            }
        }
    }

    // 올바른 버전: ReentrantLock 사용
    static class GoodTransferService {
        private final ReentrantLock lock = new ReentrantLock();

        public void transfer(TransferRequest request) throws InterruptedException {
            lock.lock();
            try {
                repository.findById(request.fromAccountId()); // I/O 대기 시 Carrier Thread 반환
                repository.save(repository.findById(request.toAccountId()));
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<TransferRequest> requests = PlatformThreadTransfer.generateRequests(100);

        System.out.println("=== [Step 3-1] Pinning 발생 (synchronized) ===");
        runWithService(requests, new BadTransferService());

        System.out.println("\n=== [Step 3-2] Pinning 해결 (ReentrantLock) ===");
        runWithService(requests, new GoodTransferService());
    }

    interface Service {
        void transfer(TransferRequest req) throws InterruptedException;
    }

    static void runWithService(List<TransferRequest> requests, Object service) throws Exception {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(requests.size());
        long start = System.currentTimeMillis();

        for (TransferRequest req : requests) {
            executor.submit(() -> {
                try {
                    if (service instanceof BadTransferService s) s.transfer(req);
                    else if (service instanceof GoodTransferService s) s.transfer(req);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        System.out.println("소요 시간: " + (System.currentTimeMillis() - start) + "ms");
    }
}
```

---

## Step 4: 전체 비교 실행

```java
package bank;

import bank.model.TransferRequest;
import bank.service.TransferService;
import bank.step1.PlatformThreadTransfer;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {
        List<TransferRequest> requests = PlatformThreadTransfer.generateRequests(1_000);

        System.out.println("========================================");
        System.out.println("  은행 이체 처리 - Virtual Thread 비교");
        System.out.println("========================================\n");

        long platformResult = runBenchmark("플랫폼 스레드 (200개)", requests,
            Executors.newFixedThreadPool(200));

        Thread.sleep(1000); // 쿨다운

        long virtualResult = runBenchmark("Virtual Thread", requests,
            Executors.newVirtualThreadPerTaskExecutor());

        System.out.println("\n========================================");
        System.out.println("  최종 비교");
        System.out.println("========================================");
        System.out.printf("플랫폼 스레드: %dms%n", platformResult);
        System.out.printf("Virtual Thread: %dms%n", virtualResult);
        System.out.printf("개선율: %.1fx 빠름%n", (double) platformResult / virtualResult);
    }

    static long runBenchmark(String label, List<TransferRequest> requests,
                              ExecutorService executor) throws Exception {
        TransferService service = new TransferService();
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(requests.size());

        long start = System.currentTimeMillis();

        for (TransferRequest req : requests) {
            executor.submit(() -> {
                try {
                    if (service.transfer(req)) success.incrementAndGet();
                    else fail.incrementAndGet();
                } catch (Exception e) {
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long elapsed = System.currentTimeMillis() - start;
        executor.shutdown();

        System.out.printf("[%s] %dms | 성공: %d | 실패: %d | 처리량: %d건/초%n",
            label, elapsed, success.get(), fail.get(),
            requests.size() * 1000L / elapsed);

        return elapsed;
    }
}
```

---

## 실행 방법

```bash
# 1. 플랫폼 스레드 문제 확인
javac -d out src/main/java/bank/**/*.java
java -cp out bank.step1.PlatformThreadTransfer

# 2. Virtual Thread 개선 확인
java -cp out bank.step2.VirtualThreadTransfer

# 3. Pinning 문제 시연 (로그 출력 포함)
java -Djdk.tracePinnedThreads=full -cp out bank.step3.PinningProblemDemo

# 4. 전체 비교 벤치마크
java -cp out bank.BenchmarkRunner
```

---

## 핵심 정리

| 항목 | 플랫폼 스레드 | Virtual Thread |
|------|-------------|----------------|
| 스레드 수 | 200개 제한 | 요청마다 생성 (수천~수만) |
| 메모리 | 스레드당 ~1MB | 스레드당 수KB |
| I/O 대기 시 | Carrier Thread 점유 | Carrier Thread 반환 |
| 처리량 | ~500건/초 | ~5000건/초 |
| 코드 변경 | - | `Executors.newVirtualThreadPerTaskExecutor()` 한 줄 |

### 주의사항
- `synchronized` → `ReentrantLock` 으로 교체 (Pinning 방지)
- CPU-bound 작업에는 효과 없음 (I/O-bound에서만 유리)
- JDK 21+ 필요

---

## 다음 단계: Spring + Transaction

순수 Java에서 Virtual Thread 동작을 이해했다면, Spring 환경에서는:
- `spring.threads.virtual.enabled=true` 설정
- `@Transactional`과 Virtual Thread 상호작용
- HikariCP 커넥션 풀 설정 조정
- `ThreadLocal` 주의사항 (Virtual Thread에서도 동작하지만 의도치 않은 공유 주의)
