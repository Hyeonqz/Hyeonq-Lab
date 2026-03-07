package org.hyeonqz.kotlinlab.kotlinlab.osiv.infrastructure

import org.hyeonqz.kotlinlab.kotlinlab.osiv.domain.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository: JpaRepository<OrderItem, Long> {
}