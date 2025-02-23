package org.hyeonqz.kafkalab.example1.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

	@KafkaListener(topics="topic-1", groupId="c-group=1")
	public void listener(Object data) {
		log.info("Received {}", data);
	}
}
