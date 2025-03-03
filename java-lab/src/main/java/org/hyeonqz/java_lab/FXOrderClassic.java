package org.hyeonqz.java_lab;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class FXOrderClassic {
	private final int units;
	private final BigDecimal currencyPair;
	private final double price;
	private final LocalDateTime sentAt;
	private final int ttl;

	public FXOrderClassic (int units, BigDecimal currencyPair, double price, LocalDateTime sentAt, int ttl) {
		this.units = units;
		this.currencyPair = currencyPair;
		this.price = price;
		this.sentAt = sentAt;
		this.ttl = ttl;
	}

	// getter, setter, toString, hashCode, equal
}
