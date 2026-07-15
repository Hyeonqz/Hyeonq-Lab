package org.hyeonqz.architecture.data;

import java.util.List;

/**
 * 아웃박스 릴레이 (실제라면 CDC/Debezium 또는 폴링 워커). Step 11 §3.
 * 아웃박스에 쌓인 이벤트를 브로커로 옮기고, 발행이 확인된 것만 지운다.
 * 발행에 실패하면 이벤트는 아웃박스에 남아 다음 실행에서 재시도된다 — 그래서 "최소 한 번"이 보장된다.
 */
public class OutboxRelay {

    private final OrderStore store;
    private final EventBroker broker;

    public OutboxRelay(OrderStore store, EventBroker broker) {
        this.store = store;
        this.broker = broker;
    }

    /** 한 번의 릴레이 실행. 발행에 성공한 이벤트만 아웃박스에서 제거한다. */
    public void flush() {
        List<OutboxEvent> pending = store.pollPending();
        for (OutboxEvent event : pending) {
            broker.publish(event);       // 실패하면 예외 → markPublished 안 됨 → 다음 flush에서 재시도
            store.markPublished(event);
        }
    }
}
