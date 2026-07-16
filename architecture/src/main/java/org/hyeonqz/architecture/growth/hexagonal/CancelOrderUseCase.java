package org.hyeonqz.architecture.growth.hexagonal;

/** 대화형 취소 유스케이스 — 규칙을 도메인에 위임한다. */
public class CancelOrderUseCase {

    private final OrderRepository repository;

    public CancelOrderUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    public void cancel(String orderId, boolean approved) {
        Order order = repository.byId(orderId);
        order.cancel(approved); // 규칙은 여기 없다 — 도메인이 지킨다
        repository.save(order);
    }
}
