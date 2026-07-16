package org.hyeonqz.architecture.growth.layered;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 대화형 취소 경로. 규칙이 여기 쌓인다.
 * 처음엔 "정산 완료 주문 취소 금지" 하나였는데, 나중에 "고액 주문 승인 필요"가 더해졌다.
 */
public class OrderService {

    static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("50000");

    private final Map<String, Order> store;

    public OrderService(Map<String, Order> store) {
        this.store = store;
    }

    public void register(String id, BigDecimal amount) {
        store.put(id, new Order(id, amount));
    }

    public void cancel(String orderId, boolean approved) {
        Order order = store.get(orderId);
        if ("SETTLED".equals(order.getStatus())) {           // 규칙 1 (원래 있던 것)
            throw new IllegalStateException("정산 완료 주문은 취소 불가: " + orderId);
        }
        if (order.getAmount().compareTo(APPROVAL_THRESHOLD) > 0 && !approved) { // 규칙 2 (나중에 추가)
            throw new ApprovalRequiredException(orderId);
        }
        order.setStatus("CANCELLED");
    }
}
