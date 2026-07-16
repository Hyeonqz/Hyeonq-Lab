package org.hyeonqz.architecture.payment.hexagonal.domain;

import java.math.BigDecimal;

/**
 * 값 객체 — 금액. 원시 BigDecimal 대신 이 타입을 쓰면 통화·반올림·검증 규칙이 한 곳에 모인다.
 * (레이어드의 원시 BigDecimal 과의 대비점 — 암묵적 개념을 명시적으로.)
 */
public record Money(BigDecimal amount) {

    public Money {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없다: " + amount);
        }
    }

    public static Money won(long value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public Money multiply(BigDecimal rate) {
        return new Money(amount.multiply(rate));
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }
}
