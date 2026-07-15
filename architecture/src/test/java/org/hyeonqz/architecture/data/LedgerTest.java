package org.hyeonqz.architecture.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * 추가 전용 원장과 역분개 (Step 11 §5). 회계의 복식부기가 곧 이벤트 소싱이었다.
 * 잔액은 저장된 값이 아니라 모든 행의 합이며, 정정은 삭제가 아니라 반대 분개다.
 */
class LedgerTest {

    @Test
    void 잔액은_모든_행의_합이다() {
        SettlementLedger ledger = new SettlementLedger();
        ledger.on(new OutboxEvent("e1", "SETTLEMENT", "A-1", new BigDecimal("10000")));
        ledger.on(new OutboxEvent("e2", "SETTLEMENT", "A-2", new BigDecimal("5000")));

        assertEquals(0, new BigDecimal("15000").compareTo(ledger.balance()));
    }

    @Test
    void 정정은_원본을_지우지_않고_역분개로_상쇄한다() {
        SettlementLedger ledger = new SettlementLedger();
        ledger.on(new OutboxEvent("e1", "SETTLEMENT", "A-1", new BigDecimal("10000")));

        // A-1 정산이 잘못됐다 → UPDATE가 아니라 역분개
        ledger.reverse("A-1", new BigDecimal("10000"));

        // 잔액은 0으로 상쇄되지만, 이력은 지워지지 않는다 — 두 행 모두 남는다 (감사 추적)
        assertEquals(0, BigDecimal.ZERO.compareTo(ledger.balance()));
        assertEquals(2, ledger.entryCount());
        assertEquals("SETTLEMENT", ledger.auditTrail().get(0).type());
        assertEquals("REVERSAL", ledger.auditTrail().get(1).type());
    }
}
