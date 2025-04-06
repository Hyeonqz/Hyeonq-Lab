package org.hyeonqz.redislab.nosql.service;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.hyeonqz.redislab.nosql.entity.PosTerminal;
import org.hyeonqz.redislab.nosql.repository.PosTerminalRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PosQueryService {
	private final PosTerminalRepository posTerminalRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	private final ObjectMapper objectMapper;

	public PosTerminal getPos(String posId) {

		Object object = redisTemplate.opsForHash().get("posTerminals", posId);

		if(object==null)
			throw new IllegalArgumentException(posId + " 는 유효하지 않습니다.");

		return objectMapper.convertValue(object, PosTerminal.class);
	}

	public int getAllPos() {
		Map<Object, Object> entries = redisTemplate.opsForHash().entries("posTerminals");

		return entries.size();
	}

}
