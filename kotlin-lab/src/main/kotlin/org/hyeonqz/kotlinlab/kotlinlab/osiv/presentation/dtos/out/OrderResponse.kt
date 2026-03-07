package org.hyeonqz.kotlinlab.kotlinlab.osiv.presentation.dtos.out

import org.hyeonqz.kotlinlab.kotlinlab.osiv.domain.Order

data class OrderResponse(
    val orderId: Long,
    val amount: Long,
    val status: String,
    val merchantName: String,   // Lazy 연관에서 가져옴
    val merchantCode: String,
    val itemCount: Int          // Lazy 컬렉션에서 가져옴
) {
    companion object {
        fun from(order: Order) = OrderResponse(
            orderId      = order.id,
            amount       = order.amount,
            status       = order.status,
            merchantName = order.merchant.name,   // ← Lazy 접근
            merchantCode = order.merchant.code,
            itemCount    = order.items.size        // ← Lazy 컬렉션 접근
        )
    }
}
