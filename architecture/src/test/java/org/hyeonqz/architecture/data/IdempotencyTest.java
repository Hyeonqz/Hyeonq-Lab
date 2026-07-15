package org.hyeonqz.architecture.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * "최소 한 번 전달 + 멱등 수신 = 정확히 한 번 효과" (Step 11 §3).
 * 브로커가 같은 이벤트를 두 번 전달해도 원장에는 한 번만 적힌다.
 */
class IdempotencyTest {

    @Test
    void 중복_전달돼도_원장에는_한_번만_적힌다() {
        OrderStore store = new OrderStore();
        EventBroker broker = new EventBroker();
        SettlementLedger ledger = new SettlementLedger();
        broker.subscribe(ledger::on); // 정산 원장이 이벤트를 구독

        ConfirmationService service = new ConfirmationService(store, broker);
        OutboxRelay relay = new OutboxRelay(store, broker);

        service.confirmWithOutbox("A-1", new BigDecimal("10000"));
        relay.flush(); // 정상 전달 1회

        // at-least-once: 브로커가 같은 이벤트를 재전달 (릴레이 재시도, 네트워크 중복 등)
        OutboxEvent duplicate = new OutboxEvent("evt-A-1", "OrderConfirmed", "A-1", new BigDecimal("10000"));
        broker.redeliver(duplicate);
        broker.redeliver(duplicate);

        // 두 번 더 전달됐지만 원장은 한 번만 기록 — eventId로 멱등 처리
        assertEquals(1, ledger.entryCount());
        assertEquals(0, new BigDecimal("10000").compareTo(ledger.balance()));
    }
}
