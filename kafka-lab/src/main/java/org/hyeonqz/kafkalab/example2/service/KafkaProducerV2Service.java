package org.hyeonqz.kafkalab.example2.service;

import java.time.LocalDateTime;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.hyeonqz.kafkalab.common.messages.KafkaMessage;
import org.hyeonqz.kafkalab.common.produceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
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

        // 지정해진 파티션에 메시지 저장
        ProducerRecord<String, Object> record = new ProducerRecord<>(
            topic, // 토픽 이름
            0, // 파티션 지정
            "payment-audit", // 키
            kafkaMessage // 메시지
        );

        this.kafkaTemplate.send(record);
        log.info("Message Sent: {}", kafkaMessage);
    }

}
