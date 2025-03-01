package org.hyeonqz.redislab.nosql.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {
	private final RedisProperties redisProperties;

	/*
	* Spring Data Redis 의존성이 있으면 RedisConnectionFactory, RedisTemplate 을 자동으로 설정해줌 -> @EnableAutoConfiguration 덕분
	* */

	/*
	* 커스터마이징 필요시 ex) 직렬화 방식 변경, 추가 설정(커넥션 풀, 타임아웃)
	* */
}
