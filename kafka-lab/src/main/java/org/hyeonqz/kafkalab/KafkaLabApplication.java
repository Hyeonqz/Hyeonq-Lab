package org.hyeonqz.kafkalab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableKafka
@EnableJpaRepositories(basePackages = {
	"org.hyeonqz.kafkalab.batch_example.v1.repository",
	"org.hyeonqz.kafkalab.batch_example.v2.repository"
})
@EntityScan(basePackages = {
	"org.hyeonqz.kafkalab.batch_example.v1.entity",
	"org.hyeonqz.kafkalab.batch_example.v2.entity"
})
@SpringBootApplication
public class KafkaLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaLabApplication.class, args);
	}

}
