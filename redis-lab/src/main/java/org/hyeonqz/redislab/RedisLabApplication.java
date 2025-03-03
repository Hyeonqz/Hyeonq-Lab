package org.hyeonqz.redislab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class RedisLabApplication {

	@PostConstruct
	public void init() {
		log.info("HyeonKyu Redis Lab Server Start");
	}

	public static void main (String[] args) {
		SpringApplication.run(RedisLabApplication.class, args);
	}

}
