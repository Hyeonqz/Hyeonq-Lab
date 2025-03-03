package org.hyeonqz.redislab.nosql.service;

import java.util.UUID;

import org.hyeonqz.redislab.nosql.entity.Pos;
import org.hyeonqz.redislab.nosql.repository.PosRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PosCommandService {
	// 단순한 쓰기: SET, HSET 사용시 작업 처리하기 좋음
	// Redis 는 단일 스레드로 동작하고, 트랜잭션이 제한적임.
	private final PosRepository posRepository;

	/* 복잡한 데이터 구조나 성능 최적화가 필요할 때 사용 */
	//private final RedisTemplate<String,Object> redisTemplate;

	@Transactional
	public void createPos() {
		UUID userId = UUID.randomUUID();
		String uuid = userId.toString();

		Pos pos = new Pos(uuid,"현규 커피 POS-1", "현규 커피", 60000L);
		posRepository.save(pos);
	}

	public void bulkInsert() {

	}
}
