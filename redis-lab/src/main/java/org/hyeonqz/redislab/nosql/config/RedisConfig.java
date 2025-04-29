package org.hyeonqz.redislab.nosql.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
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

	/** 커스터마이징 필요시 ex) 직렬화 방식 변경, 추가 설정(커넥션 풀, 타임아웃) -> application.yml 데이터 redis 에 주입 */
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		// Redis 와 연결을 위한 Connection 생성 관리 메소드
		return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
	}

	/** Redis 데이터 처리를 위한 템플릿을 구성하는 메소드 -> Redis 데이터 직렬화 역직렬화 수행 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

		// redis 연결
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		// [set] key-value 형태로 직렬화 수행
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		// [Hash] key,value 를 json 으로 직렬화 & 역직렬화 하기 위한 설정
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
		redisTemplate.setHashKeySerializer(serializer);
		redisTemplate.setHashValueSerializer(serializer);

		// default 직렬화 수행
		redisTemplate.setDefaultSerializer(new StringRedisSerializer());

		return redisTemplate;
	}

/*	@Bean
	public RedisTemplate<String, String> redisTemplate() {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());

		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}*/
}
