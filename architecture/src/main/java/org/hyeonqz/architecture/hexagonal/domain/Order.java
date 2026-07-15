package org.hyeonqz.architecture.hexagonal.domain;

import java.math.BigDecimal;

/**
 * 리치 도메인 모델 — 규칙이 개념과 동거한다.
 * 불변식을 지킬 단일 장소가 이 클래스다 (Step 06→07). 바깥 세상(저장, 원장, 화면)을 전혀 모른다.
 */
public class Order {

    public enum Status { CREATED, SETTLED, CANCELLED }

    private static final BigDecimal FEE_RATE = new BigDecimal("0.03");

    private final String id;
    private final BigDecimal amount;
    private Status status = Status.CREATED;

    public Order(String id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public void cancel() {
        if (status == Status.SETTLED) {
            throw new IllegalStateException("정산 완료된 주문은 취소할 수 없다: " + id);
        }
        status = Status.CANCELLED;
    }

    public Settlement settle() {
        if (status != Status.CREATED) {
            throw new IllegalStateException("정산할 수 없는 상태다: " + id + " (" + status + ")");
        }
        status = Status.SETTLED;
        return new Settlement(id, amount, amount.multiply(FEE_RATE));
    }

    public String id() {
        return id;
    }

    public Status status() {
        return status;
    }
}
