package org.hyeonqz.kotlinlab.kotlinlab.osiv.shared

import org.hyeonqz.kotlinlab.kotlinlab.osiv.domain.Merchant
import org.hyeonqz.kotlinlab.kotlinlab.osiv.domain.Order
import org.hyeonqz.kotlinlab.kotlinlab.osiv.domain.OrderItem
import org.hyeonqz.kotlinlab.kotlinlab.osiv.infrastructure.MerchantRepository
import org.hyeonqz.kotlinlab.kotlinlab.osiv.infrastructure.OrderRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataInitializer(
    private val merchantRepository: MerchantRepository,
    private val orderRepository: OrderRepository
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments) {
        if (orderRepository.count() > 0) return

        // 가맹점 1개 생성
        val merchant = merchantRepository.save(
            Merchant(name = "세븐일레븐 강동점", code = "SE-001")
        )

        // 주문 100개 + 아이템 각 3개 생성
        repeat(100) { i ->
            val order = Order(
                amount = (i + 1) * 1000L,
                status = "COMPLETED",
                merchant = merchant
            )
            val saved = orderRepository.save(order)

            repeat(3) { j ->
                saved.items.add(
                    OrderItem(
                        productName = "상품_${i}_${j}",
                        price = 1000L,
                        order = saved
                    )
                )
            }
            orderRepository.save(saved)
        }
        println("✅ 테스트 데이터 초기화 완료: Order 100건, Item 300건")
    }
}