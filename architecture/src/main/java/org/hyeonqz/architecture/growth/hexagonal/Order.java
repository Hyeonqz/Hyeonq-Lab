package org.hyeonqz.architecture.growth.hexagonal;

import java.math.BigDecimal;

/**
 * 시나리오 C — 규칙이 쌓이는 세계의 헥사고날 버전.
 * 리치 도메인: 취소 규칙이 이 객체 하나에 산다. 규칙 2(고액 승인)가 추가돼도 여기 한 곳만 바뀐다.
 * 그리고 취소하려면 반드시 이 메서드를 거쳐야 하므로 — 어떤 진입점도 규칙을 우회할 수 없다.
 */
public class Order {

    public enum Status { CREATED, SETTLED, CANCELLED }

    static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("50000");

    private final String id;
    private final BigDecimal amount;
    private Status status = Status.CREATED;

    public Order(String id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public void cancel(boolean approved) {
        if (status == Status.SETTLED) {                        // 규칙 1
            throw new IllegalStateException("정산 완료 주문은 취소 불가: " + id);
        }
        if (amount.compareTo(APPROVAL_THRESHOLD) > 0 && !approved) { // 규칙 2 — 여기 한 곳에만 산다
            throw new ApprovalRequiredException(id);
        }
        status = Status.CANCELLED;
    }

    public String id() { return id; }
    public Status status() { return status; }
    public BigDecimal amount() { return amount; }
}
