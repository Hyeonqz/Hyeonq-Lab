package org.hyeonqz.redislab.nosql.service;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.hyeonqz.redislab.nosql.entity.PosTerminal;
import org.hyeonqz.redislab.nosql.repository.PosTerminalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PosTerminalCommandServiceTest {

	@Autowired
	private PosTerminalRepository posTerminalRepository;

	@Autowired
	private PosCommandService posCommandService;

	@Test
	@DisplayName("Redis 에 단건 save 를 한다.")
	void insertTest() {
		UUID userId = UUID.randomUUID();
		String posId = userId.toString();

		PosTerminal posTerminal = PosTerminal.builder()
			.id(3L)
			.posName("현규포스-3")
			.posId(posId)
			.build();

		PosTerminal pos = posCommandService.createPos(posTerminal);

		Assertions.assertThat(pos).isNotNull();
	}

	@Test
	@DisplayName("Redis 다건 Save")
	void multipleCreatePosTest() {
		posCommandService.multipleCreatePos();
	}

/*	@Test
	@DisplayName("RedisTemplate 직접 사용하여 Insert")
	void redisTemplateInsertTest() {
	    // given
		PosTerminal posTerminalA = null;
		List<PosTerminal> posTerminalList = new ArrayList<>();
		// when
		for (long i = 0; i < 100; i++) {
			UUID uuid = UUID.randomUUID();
			posTerminalA = new PosTerminal(uuid.toString(), "현규POS-"+i, i);
			posTerminalList.add(posTerminalA);
		}

		for (PosTerminal posTerminal : posTerminalList) {
			String key = "posTerminal:" + posTerminal.getPosId();
			redisTemplate.opsForHash().putAll(key, Map.of(
				"posName", posTerminal.getPosName(),
				"merchantId", posTerminal.getMerchantId()
			));
		}

	}*/

}