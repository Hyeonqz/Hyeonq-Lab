package org.hyeonqz.kotlinlab.kotlinlab.osiv.presentation

import org.hyeonqz.kotlinlab.kotlinlab.osiv.application.OrderService
import org.hyeonqz.kotlinlab.kotlinlab.osiv.presentation.dtos.out.OrderResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {

    // ──────────────────────────────────────────────────────────
    //  벤치마크 엔드포인트 A — OSIV=true 환경 시뮬레이션
    //  컨트롤러에서 엔티티 직접 접근 (Lazy 필드 접근)
    //  → OSIV=true  : 정상 동작 (커넥션 오래 점유)
    //  → OSIV=false : LazyInitializationException
    // ──────────────────────────────────────────────────────────
    @GetMapping("/raw/{id}")
    fun getRaw(@PathVariable id: Long): Map<String, Any> {
        val order = orderService.findOrderRaw(id)

        // 🔴 트랜잭션 밖 — Lazy 접근 발생 지점
        return mapOf(
            "orderId"      to order.id,
            "amount"       to order.amount,
            "merchantName" to order.merchant.name,    // ← Lazy
            "itemCount"    to order.items.size         // ← Lazy
        )
    }

    // ──────────────────────────────────────────────────────────
    //  벤치마크 엔드포인트 B — OSIV=false 안전 패턴
    //  Service에서 DTO 변환 완료 후 반환
    //  → OSIV=true / false 모두 정상 동작
    // ──────────────────────────────────────────────────────────
    @GetMapping("/dto/{id}")
    fun getDto(@PathVariable id: Long): OrderResponse {
        return orderService.findOrderDto(id)
    }
}