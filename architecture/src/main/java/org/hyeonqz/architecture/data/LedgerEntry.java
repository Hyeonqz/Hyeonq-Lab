package org.hyeonqz.architecture.data;

import java.math.BigDecimal;

/** 원장의 한 줄. 불변이며, 한 번 적히면 고쳐지지 않는다. */
public record LedgerEntry(String orderId, BigDecimal amount, String type) {
}
