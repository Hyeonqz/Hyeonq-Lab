package org.hyeonqz.architecture.payment.clean.entity;

import java.math.BigDecimal;

/**
 * 엔티티 계층(Enterprise Business Rules) — 승인/취소/정산 규칙이 여기 산다.
 * 헥사고날의 도메인 Payment 와 사실상 같다. 클린과 헥사고날의 차이는 원리가 아니라 어휘/강조점이다.
 */
public class Payment {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.03");

    private final String id;
    private final Money amount;
    private PaymentStatus status;
    private Money fee;

    private Payment(String id, Money amount, PaymentStatus status, Money fee) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.fee = fee;
    }

    public static Payment approve(String id, Money amount) {
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("승인 금액은 양수여야 한다: " + amount.amount());
        }
        return new Payment(id, amount, PaymentStatus.APPROVED, null);
    }

    public static Payment reconstitute(String id, Money amount, PaymentStatus status, Money fee) {
        return new Payment(id, amount, status, fee);
    }

    public void cancel() {
        if (status != PaymentStatus.APPROVED) {
            throw new IllegalStateException("승인 상태에서만 취소할 수 있다: " + status);
        }
        status = PaymentStatus.CANCELLED;
    }

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
