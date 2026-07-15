package org.hyeonqz.architecture.layered.service;

import java.math.BigDecimal;

import org.hyeonqz.architecture.layered.entity.Order;
import org.hyeonqz.architecture.layered.repository.MySqlOrderRepository;

/**
 * 트랜잭션 스크립트 — 모든 규칙이 여기에 산다.
 * 화살표를 보라: 서비스(정책) → MySqlOrderRepository(세부사항). 정책이 세부사항의 인질이다 (Step 06).
 */
public class OrderService {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.03");

    private final MySqlOrderRepository orderRepository = new MySqlOrderRepository();

    public void register(String orderId, BigDecimal amount) {
        orderRepository.save(new Order(orderId, amount));
    }

    public void cancel(String orderId) {
        Order order = orderRepository.findById(orderId);
        if ("SETTLED".equals(order.getStatus())) { // 규칙이 서비스에 산다
            throw new IllegalStateException("정산 완료된 주문은 취소할 수 없다: " + orderId);
        }
        order.setStatus("CANCELLED"); // 객체는 시키는 대로 할 뿐
        orderRepository.save(order);
    }

    public BigDecimal settle(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (!"CREATED".equals(order.getStatus())) {
            throw new IllegalStateException("정산할 수 없는 상태다: " + orderId);
        }
        order.setStatus("SETTLED");
        orderRepository.save(order);
        BigDecimal fee = order.getAmount().multiply(FEE_RATE); // 수수료 규칙도 여기에
        System.out.println("[정산] order=" + orderId + " fee=" + fee); // 원장 기록마저 여기에
        return fee;
    }
}
