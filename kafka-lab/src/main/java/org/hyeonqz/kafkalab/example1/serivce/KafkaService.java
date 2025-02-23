package org.hyeonqz.kafkalab.example1.serivce;

import org.hyeonqz.kafkalab.example1.consumer.KafkaConsumer;
import org.hyeonqz.kafkalab.example1.producer.KafkaProducer;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaService {
	private final KafkaProducer kafkaProducer;
	private final KafkaConsumer kafkaConsumer;

	public void getKafkaMessage() {
		kafkaProducer.createMessage();
	}



}
