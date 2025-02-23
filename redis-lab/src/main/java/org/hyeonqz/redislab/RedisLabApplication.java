package org.hyeonqz.redislab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class RedisLabApplication {

	@PostConstruct
	public void init() {
		log.info("Redis Lab Server Start");
	}
	public static void main (String[] args) {
		SpringApplication.run(RedisLabApplication.class, args);
	}

}
