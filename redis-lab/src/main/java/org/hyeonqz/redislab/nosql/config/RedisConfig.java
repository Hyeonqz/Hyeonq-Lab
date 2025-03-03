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
	* redis-cli 를 직접 이용하려면 위 설정을 Bean 으로 잡아줘야함.
	* */

	/*
	* 커스터마이징 필요시 ex) 직렬화 방식 변경, 추가 설정(커넥션 풀, 타임아웃)
	* */
	// application.yml 데이터 redis 에 주입
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
	}

	// redis 통해 직접 데이터 조회 설정
/*	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		return redisTemplate;
	}*/

	@Bean
	public RedisTemplate<String, String> redisTemplate() {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());

		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}
}
