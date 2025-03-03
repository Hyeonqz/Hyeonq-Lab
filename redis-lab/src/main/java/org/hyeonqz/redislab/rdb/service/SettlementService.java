package org.hyeonqz.redislab.rdb.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.hyeonqz.redislab.rdb.entity.Settlement;
import org.hyeonqz.redislab.rdb.repository.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SettlementService {
	private final SettlementRepository settlementRepository;

	@Transactional
	public void create() {
		Settlement settlement = Settlement.builder()
			.uuid(UUID.randomUUID())
			.amount(BigDecimal.valueOf(1000000L))
			.charge(BigDecimal.valueOf(2.0))
			.date(LocalDate.now())
			.createdAt(LocalDateTime.now())
			.build();

		settlementRepository.save(settlement);
	}

}
