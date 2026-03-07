package org.hyeonqz.kotlinlab.kotlinlab.osiv.infrastructure

import org.hyeonqz.kotlinlab.kotlinlab.osiv.domain.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderRepository : JpaRepository<Order, Long> {

    // ── OSIV=true 시나리오용: Lazy 그대로 조회 ──────────────
    fun findOrderById(id: Long): Order?

    // ── OSIV=false 해결 패턴: Fetch Join ──────────────────────
    @Query("""
        SELECT o FROM Order o
        JOIN FETCH o.merchant
        LEFT JOIN FETCH o.items
        WHERE o.id = :id
    """)
    fun findByIdWithFetchJoin(id: Long): Order?
}