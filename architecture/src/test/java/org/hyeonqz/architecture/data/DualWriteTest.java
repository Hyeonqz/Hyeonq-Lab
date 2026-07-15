package org.hyeonqz.architecture.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * 이중 쓰기 문제와 아웃박스 처방을 대조 시연한다 (Step 11 §3).
 * "이중 쓰기는 코드 품질이 아니라 물리학이다" — 잘 짜서 피하는 게 아니라 구조로 피한다.
 */
class DualWriteTest {

    private static final BigDecimal AMOUNT = new BigDecimal("10000");

    @Test
    void naive_경로는_발행_실패시_이벤트를_영영_잃는다() {
        OrderStore store = new OrderStore();
        EventBroker broker = new EventBroker();
        ConfirmationService service = new ConfirmationService(store, broker);

        broker.failNextPublish(); // 주문 저장 후 발행 직전에 크래시

        // 주문은 저장됐지만 발행에서 터진다
        assertThrows(RuntimeException.class, () -> service.confirmNaive("A-1", AMOUNT));

        // 결과: 주문은 있는데(쓰기 1 성공) 이벤트는 없다(쓰기 2 실패) — 정산은 이 주문을 영영 모른다
        assertTrue(store.hasOrder("A-1"));
        assertEquals(0, broker.publishedCount());
    }

    @Test
    void 아웃박스_경로는_크래시_후에도_이벤트가_DB에_살아남는다() {
        OrderStore store = new OrderStore();
        EventBroker broker = new EventBroker();
        ConfirmationService service = new ConfirmationService(store, broker);
        OutboxRelay relay = new OutboxRelay(store, broker);

        // 주문 + 이벤트를 한 트랜잭션으로 커밋 (발행은 아직 안 함)
        service.confirmWithOutbox("A-1", AMOUNT);
        assertTrue(store.hasOrder("A-1"));
        assertEquals(1, store.pendingCount()); // 이벤트가 DB(아웃박스)에 durable하게 있다

        // 릴레이의 첫 시도가 브로커 단절로 실패해도 — 이벤트는 아웃박스에 남는다
        broker.failNextPublish();
        assertThrows(RuntimeException.class, relay::flush);
        assertEquals(1, store.pendingCount()); // 아직 안 지워짐 → 유실 없음

        // 브로커 복구 후 재시도 → 발행 성공, 아웃박스 비워짐
        relay.flush();
        assertEquals(1, broker.publishedCount());
        assertEquals(0, store.pendingCount());
    }
}
