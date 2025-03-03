package org.hyeonqz.redislab.rdb.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hyeonqz.redislab.RedisLabApplication;
import org.hyeonqz.redislab.rdb.entity.Settlement;
import org.hyeonqz.redislab.rdb.repository.SettlementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@SpringBootTest(classes = RedisLabApplication.class)
class SettlementServiceTest {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private SettlementService settlementService;

	@Autowired
	private SettlementRepository settlementRepository;

	@Autowired
	private EntityManager entityManager;


	@Test
	@DisplayName("JPA 단건 Insert")
	void Single_Settlement_Insert_Test() {
		// given & when & then
		settlementService.create();
	}

	@Test
	@Transactional(rollbackFor = Exception.class)
	@Commit // 롤백되지 않고 커밋됨
	@DisplayName("JPA BulkInsert -> avg: 1min 2sec, batch_size 조절: 53sec")
	void JPA_Bulk_Insert_Test() {
		int batchSize = 5000;
		// given
		List<Settlement> settlements = new ArrayList<>(batchSize);

		// when
		for (int i = 0; i < 120000; i++) {
			Settlement settlement = Settlement.builder()
				.uuid(UUID.randomUUID())
				.amount(BigDecimal.valueOf(1000000L))
				.charge(BigDecimal.valueOf(2.0))
				.date(LocalDate.now())
				.createdAt(LocalDateTime.now())
				.build();

			settlements.add(settlement);

			if(settlements.size() >= batchSize) {
				settlementRepository.saveAll(settlements);
				entityManager.flush();
				entityManager.clear();
				settlements.clear();
			}
		}
		if(!settlements.isEmpty()) {
			settlementRepository.saveAll(settlements);
			entityManager.flush();
			entityManager.clear();
		}

	}

	@DisplayName("JDBC Template 을 사용하여 10만건 데이터를 Insert 한다.")
	void JdbcTemplate_Bulk_Insert_Test() {
	    // given
		List<Object[]> batchArgs = new ArrayList<>();

		int batchSize = 5000;

	    // when
		for (int i = 0; i < 100000; i++) {
			UUID uuid = UUID.randomUUID();
			BigDecimal amount = BigDecimal.valueOf(1000000L);
			BigDecimal charge = BigDecimal.valueOf(2.0);
			LocalDate date = LocalDate.now();
			LocalDateTime createdAt = LocalDateTime.now();

			Object[] values = new Object[] {uuid, amount, charge, date, createdAt};
			batchArgs.add(values);
		}

		// batchSize에 도달할 때마다 배치 실행
		if (batchArgs.size() == batchSize) {
			jdbcTemplate.batchUpdate(
				"INSERT INTO settlement (uuid, amount, charge, date, createdAt) VALUES (?, ?, ?, ?, ?)",
				batchArgs
			);
			batchArgs.clear(); // 메모리 확보를 위해 리스트 비우기
		}

		if (!batchArgs.isEmpty()) {
			jdbcTemplate.batchUpdate(
				"INSERT INTO settlement (uuid, amount, charge, date, createdAt) VALUES (?, ?, ?, ?, ?)",
				batchArgs
			);
		}

	}

}