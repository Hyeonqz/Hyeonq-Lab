package org.hyeonqz.kafkalab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableKafka
@SpringBootApplication
public class KafkaLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaLabApplication.class, args);
	}

}
