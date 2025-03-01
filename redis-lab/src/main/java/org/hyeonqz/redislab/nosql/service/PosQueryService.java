package org.hyeonqz.redislab.nosql.service;

import java.util.List;

import org.hyeonqz.redislab.nosql.entity.Pos;
import org.hyeonqz.redislab.nosql.repository.PosRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PosQueryService {
	private final PosRepository posRepository;
	private final RedisTemplate<String, Object> redisTemplate;

}
