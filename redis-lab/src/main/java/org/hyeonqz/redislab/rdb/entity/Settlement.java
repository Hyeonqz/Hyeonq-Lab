package org.hyeonqz.redislab.rdb.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Settlement {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private UUID uuid;

	private BigDecimal amount;
	private BigDecimal charge;
	private LocalDate date;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder
	public Settlement (UUID uuid, BigDecimal amount, BigDecimal charge, LocalDate date, LocalDateTime createdAt) {
		this.uuid = uuid;
		this.amount = amount;
		this.charge = charge;
		this.date = date;
		this.createdAt = createdAt;
	}

}
