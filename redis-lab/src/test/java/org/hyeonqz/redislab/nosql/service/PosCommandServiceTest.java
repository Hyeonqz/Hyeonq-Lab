package org.hyeonqz.redislab.nosql.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.hyeonqz.redislab.nosql.entity.Pos;
import org.hyeonqz.redislab.nosql.repository.PosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class PosCommandServiceTest {

	@Autowired
	private PosRepository posRepository;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@BeforeEach
	void setUp () {
		posRepository.deleteAll();
	}

	@Test
	@DisplayName("Redis 에 Bulk save 를 한다.")
	void insertTest() {
	    // given
		Pos pos = null;
		List<Pos> posList = new ArrayList<>();
	    // when
		for (int i = 0; i < 100; i++) {
			UUID uuid = UUID.randomUUID();
			pos = new Pos(uuid.toString(), "현규POS-"+i, "현규 커피", 64000L);
			posList.add(pos);
		}

	    // then
		posRepository.saveAll(posList);
	}

	@Test
	@DisplayName("RedisTemplate 직접 사용하여 Insert")
	void redisTemplateInsertTest() {
	    // given
		Pos posA = null;
		List<Pos> posList = new ArrayList<>();
		// when
		for (int i = 0; i < 100; i++) {
			UUID uuid = UUID.randomUUID();
			posA = new Pos(uuid.toString(), "현규POS-"+i, "현규 커피", 64000L);
			posList.add(posA);
		}

		for (Pos pos : posList) {
			String key = "pos:" + pos.getPosId();
			redisTemplate.opsForHash().putAll(key, Map.of(
				"posName", pos.getPosName(),
				"merchantName", pos.getMerchantName(),
				"expiration", pos.getExpiration().toString()
			));
			redisTemplate.expire(key, pos.getExpiration(), TimeUnit.SECONDS);
		}

	    // then
	}

}