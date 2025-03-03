package org.hyeonqz.redislab.batch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hyeonqz.redislab.rdb.entity.Settlement;
import org.hyeonqz.redislab.rdb.entity.SettlementHistory;
import org.hyeonqz.redislab.rdb.repository.SettlementHistoryRepository;
import org.hyeonqz.redislab.rdb.repository.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BatchService {
	private final SettlementRepository settlementRepository;
	private final SettlementHistoryRepository settlementHistoryRepository;

	@Transactional
	public void execute() {
		LocalDate date = LocalDate.now();

		List<Settlement> byDate = settlementRepository.findByDate(date);

		BigDecimal totalAmount = byDate.stream()
			.map(Settlement::getAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		log.info("Total amount: {}", totalAmount);

		SettlementHistory settlementHistory = SettlementHistory.builder()
			.amount(totalAmount)
			.settlementTotalAmount(totalAmount.multiply(byDate.getFirst().getCharge()))
			.localDateTime(LocalDateTime.now())
			.build();

		settlementHistoryRepository.save(settlementHistory);
	}
}
