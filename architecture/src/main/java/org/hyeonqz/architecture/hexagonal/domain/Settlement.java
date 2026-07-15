package org.hyeonqz.architecture.hexagonal.domain;

import java.math.BigDecimal;

/** 정산 결과 — 값 객체. */
public record Settlement(String orderId, BigDecimal amount, BigDecimal fee) {
}
