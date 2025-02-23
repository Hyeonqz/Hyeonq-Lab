package org.hyeonqz.kafkalab.example1.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
/*	@Value(value="${}")
	private String bootstrapAddress;

	@Bean
	public KafkaAdmin kafkaAdmin() {
		Map<String,Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic topic() {
		return new NewTopic("hkjin-topic-1",1,(short) 1);
	}*/
}
