package org.hyeonqz.kafkalab.batch_example.v1.service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.hyeonqz.kafkalab.common.messages.KafkaMessage;
import org.hyeonqz.kafkalab.common.produceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducerV2Service implements produceService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${hkjin.kafka.topics.audit.name}")
    private String topic;

    @Override
    public void produceMessage () {
        LocalDateTime now = LocalDateTime.now();
        KafkaMessage kafkaMessage = new KafkaMessage(
            "hello?",
            "kafka-batch-test",
            now,
            LocalDateTime.now().plusSeconds(1)
            );

        this.kafkaTemplate.send(topic, kafkaMessage);

        log.info("Message Sent: {}", kafkaMessage);
    }

}
