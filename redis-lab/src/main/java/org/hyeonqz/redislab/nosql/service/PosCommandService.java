package org.hyeonqz.redislab.nosql.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hyeonqz.redislab.nosql.entity.PosTerminal;
import org.hyeonqz.redislab.nosql.repository.PosTerminalRepository;
import org.hyeonqz.redislab.rdb.entity.Pos;
import org.hyeonqz.redislab.rdb.repository.PosRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PosCommandService {
	// 단순한 쓰기: SET, HSET 사용시 작업 처리하기 좋음
	// Redis 는 단일 스레드로 동작하고, 트랜잭션이 제한적임.
	private final PosTerminalRepository posTerminalRepository;
	private final PosRepository posRepository;
	private final RedisTemplate<String,Object> redisTemplate;

	public PosTerminal createPos(PosTerminal posTerminal) {
		String key = String.format("terminal:%s",posTerminal.getPosId());

		redisTemplate.opsForHash().put("posTerminals", key, posTerminal);
		return posTerminal;
	}

	@Transactional
	public void multipleCreatePos() {
		List<Pos> all = posRepository.findAll();
		Map<String, PosTerminal> posTerminalMap = new HashMap<>();

		var list = new ArrayList<PosTerminal>();

		PosTerminal terminal;
		long i = 1L;
		String key = null;
		for(Pos pos : all) {
			terminal = PosTerminal.builder()
				.posId(pos.getUuid().toString())
				.posName("HK POS-"+i)
				.id(i)
				.build();

			key = String.format("terminal:%s",terminal.getPosId());

			posTerminalMap.put(key, terminal);
			i++;
			if (i == 400000L) break;
		}

		redisTemplate.opsForHash().putAll("posTerminals", posTerminalMap);
	}
}
