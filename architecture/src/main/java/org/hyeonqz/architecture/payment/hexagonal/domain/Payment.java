package org.hyeonqz.architecture.payment.hexagonal.domain;

import java.math.BigDecimal;

/**
 * 리치 도메인 모델(애그리거트 루트) — 승인/취소/정산 규칙이 개념과 동거한다.
 * 불변식을 지킬 단일 장소가 이 클래스다. 바깥 세상(JPA, 브로커, 화면)을 전혀 모른다.
 */
public class Payment {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.03");

    private final String id;
    private final Money amount;
    private PaymentStatus status;
    private Money fee; // 정산 시 확정, 그 전엔 null

    private Payment(String id, Money amount, PaymentStatus status, Money fee) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.fee = fee;
    }

    /** 승인 — 금액이 양수인 결제를 APPROVED 로 생성한다. */
    public static Payment approve(String id, Money amount) {
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("승인 금액은 양수여야 한다: " + amount.amount());
        }
        return new Payment(id, amount, PaymentStatus.APPROVED, null);
    }

    /** 저장소에서 복원할 때 쓰는 재구성 팩토리(규칙 검증 없이 상태 그대로). */
    public static Payment reconstitute(String id, Money amount, PaymentStatus status, Money fee) {
        return new Payment(id, amount, status, fee);
    }

    /** 취소 — 승인 상태에서만 가능하다. */
    public void cancel() {
        if (status != PaymentStatus.APPROVED) {
            throw new IllegalStateException("승인 상태에서만 취소할 수 있다: " + status);
        }
        status = PaymentStatus.CANCELLED;
    }

    /** 정산 — 승인 상태에서만 가능하고, 수수료(3%)를 계산해 확정한다. */
    public Money settle() {
        if (status != PaymentStatus.APPROVED) {
            throw new IllegalStateException("승인 상태에서만 정산할 수 있다: " + status);
        }
        status = PaymentStatus.SETTLED;
        fee = amount.multiply(FEE_RATE);
        return fee;
    }

    public String id() { return id; }
    public Money amount() { return amount; }
    public PaymentStatus status() { return status; }
    public Money fee() { return fee; }
}
