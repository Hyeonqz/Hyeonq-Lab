package org.hyeonqz.architecture.payment.clean.entity;

import java.math.BigDecimal;

/** 값 객체 — 헥사고날의 Money 와 같다. 스타일이 달라도 도메인 원리는 같다는 것을 보여주는 대비점. */
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
