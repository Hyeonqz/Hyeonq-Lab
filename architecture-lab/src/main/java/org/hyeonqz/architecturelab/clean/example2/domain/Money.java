package org.hyeonqz.architecturelab.clean.example2.domain;

import java.math.BigDecimal;

public record Money(BigDecimal amount) {

    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money add(Money a, Money b) {
        return new Money(a.amount.add(b.amount));
    }

    public Money negate() {
        return new Money(amount.negate());
    }

    // 오타 유지 (Account.java 에서 isPositivie 로 호출 중)
    public boolean isPositivie() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(long value) {
        return amount.compareTo(BigDecimal.valueOf(value)) > 0;
    }
}
