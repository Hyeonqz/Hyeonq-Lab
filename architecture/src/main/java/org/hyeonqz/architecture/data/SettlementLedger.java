package org.hyeonqz.architecture.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 정산 원장 — 이 클래스 하나에 Step 11의 세 개념이 모두 들어 있다.
 *
 * 1) 멱등 수신(idempotent consumer): 이미 처리한 eventId는 무시한다.
 *    "최소 한 번 전달 + 멱등 수신 = 정확히 한 번 효과".
 * 2) 추가 전용(append-only): 기입은 add만, 수정/삭제는 없다.
 * 3) 역분개(reverse entry): 정정은 원본을 고치는 게 아니라 반대 부호의 행을 더한다.
 *    잔액은 언제나 모든 행의 합이다 (이벤트 소싱/복식부기의 규율).
 */
public class SettlementLedger {

    private final List<LedgerEntry> entries = new ArrayList<>(); // 추가 전용
    // 멱등성 키. ponytail: 메모리 집합이라 무한히 자란다 — 실제라면 처리 이력 테이블 + TTL로 경계를 둔다.
    private final Set<String> processedEventIds = new HashSet<>();

    /** 브로커가 전달한 이벤트를 처리한다. 중복 전달은 여기서 걸러진다. */
    public void on(OutboxEvent event) {
        if (processedEventIds.contains(event.eventId())) {
            return; // 이미 처리함 — 멱등
        }
        processedEventIds.add(event.eventId());
        entries.add(new LedgerEntry(event.orderId(), event.amount(), "SETTLEMENT"));
    }

    /** 정정: 원본을 지우지 않고 역분개 행을 더한다. 이력이 보존된다. */
    public void reverse(String orderId, BigDecimal amount) {
        entries.add(new LedgerEntry(orderId, amount.negate(), "REVERSAL"));
    }

    /** 잔액 = 모든 행의 합. 저장된 상태가 아니라 사건의 재생이다. */
    public BigDecimal balance() {
        return entries.stream()
                .map(LedgerEntry::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int entryCount() {
        return entries.size();
    }

    public List<LedgerEntry> auditTrail() {
        return List.copyOf(entries);
    }
}
