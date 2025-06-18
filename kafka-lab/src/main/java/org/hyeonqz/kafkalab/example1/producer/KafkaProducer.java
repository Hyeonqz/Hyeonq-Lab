package org.hyeonqz.kafkalab.example1.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KafkaProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void createMessage() {
		kafkaTemplate.send("topic-1", new String("hello kafka"));
	}
}
