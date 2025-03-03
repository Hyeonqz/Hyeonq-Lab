package org.hyeonqz.redislab.rdb.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SettlementHistory {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private BigDecimal amount;

	private BigDecimal settlementTotalAmount;

	private LocalDateTime localDateTime;

	@Builder
	public SettlementHistory (BigDecimal amount, BigDecimal settlementTotalAmount, LocalDateTime localDateTime) {
		this.amount = amount;
		this.settlementTotalAmount = settlementTotalAmount;
		this.localDateTime = localDateTime;
	}

}
