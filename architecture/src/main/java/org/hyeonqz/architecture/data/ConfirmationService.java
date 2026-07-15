package org.hyeonqz.architecture.data;

import java.math.BigDecimal;

/**
 * 주문 확정 유스케이스. 두 경로를 나란히 제공해 이중 쓰기 문제를 대조한다 (Step 11 §3).
 *
 * - confirmNaive: 주문을 저장하고, 그 다음 별도로 이벤트를 발행한다.
 *   두 번의 쓰기가 서로 다른 시스템(DB, 브로커)이라 하나의 트랜잭션으로 묶이지 않는다.
 *   저장 성공 후 발행 실패 시 → 주문은 있는데 정산은 영원히 모른다.
 *
 * - confirmWithOutbox: 주문과 이벤트를 같은 DB에 한 트랜잭션으로 커밋한다.
 *   발행은 릴레이가 나중에 아웃박스에서 옮긴다. 크래시가 나도 이벤트는 DB에 남아 있다.
 */
public class ConfirmationService {

    private final OrderStore store;
    private final EventBroker broker;

    public ConfirmationService(OrderStore store, EventBroker broker) {
        this.store = store;
        this.broker = broker;
    }

    /** 위험한 길 — 이중 쓰기. */
    public void confirmNaive(String orderId, BigDecimal amount) {
        store.confirmOnly(orderId, amount);                                  // 쓰기 1: DB
        broker.publish(new OutboxEvent(orderId, "OrderConfirmed", orderId, amount)); // 쓰기 2: 브로커 — 실패하면?
    }

    /** 안전한 길 — 트랜잭셔널 아웃박스. */
    public void confirmWithOutbox(String orderId, BigDecimal amount) {
        OutboxEvent event = new OutboxEvent("evt-" + orderId, "OrderConfirmed", orderId, amount);
        store.confirmWithOutbox(orderId, amount, event); // 한 트랜잭션: 주문 + 이벤트
        // 발행은 여기서 하지 않는다 — 릴레이의 몫이다.
    }
}
