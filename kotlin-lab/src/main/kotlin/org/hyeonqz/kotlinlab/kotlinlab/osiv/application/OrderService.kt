package org.hyeonqz.kotlinlab.kotlinlab.osiv.application

import org.hyeonqz.kotlinlab.kotlinlab.osiv.domain.Order
import org.hyeonqz.kotlinlab.kotlinlab.osiv.infrastructure.OrderRepository
import org.hyeonqz.kotlinlab.kotlinlab.osiv.presentation.dtos.out.OrderResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository
) {

    // ──────────────────────────────────────────────────────────
    //  시나리오 A — OSIV=true 환경에서 동작하는 코드
    //  트랜잭션 밖(Controller)에서 Lazy 접근
    //  → OSIV=false 로 바꾸면 LazyInitializationException 발생
    // ──────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    fun findOrderRaw(id: Long): Order {
        return orderRepository.findOrderById(id)
            ?: throw NoSuchElementException("Order not found: $id")
        // 엔티티 그대로 반환 — Lazy 필드는 아직 로딩 안 됨
    }

    // ──────────────────────────────────────────────────────────
    //  시나리오 B — OSIV=false 에서도 안전한 코드 (권장 패턴)
    //  트랜잭션 안에서 DTO로 변환 완료 후 반환
    // ──────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    fun findOrderDto(id: Long): OrderResponse {
        val order = orderRepository.findByIdWithFetchJoin(id)
            ?: throw NoSuchElementException("Order not found: $id")
        // 트랜잭션 안에서 Lazy 접근 → 정상
        return OrderResponse.from(order)
    }
}