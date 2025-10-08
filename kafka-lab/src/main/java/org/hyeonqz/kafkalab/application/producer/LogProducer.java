package org.hyeonqz.kafkalab.application.producer;

import lombok.RequiredArgsConstructor;
import org.hyeonqz.kafkalab.domain.dto.LogRequestDto;
import org.hyeonqz.kafkalab.domain.service.ProduceService;
import org.hyeonqz.kafkalab.shared.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("logProducer")
@RequiredArgsConstructor
public class LogProducer implements ProduceService<LogRequestDto> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${hkjin.kafka.topics.log.name}")
    private String topic;

    @Override
    public void produceMessage(LogRequestDto dto) {
        String correlationId = UUID.randomUUID().toString();
        KafkaMessage<LogRequestDto> message = KafkaMessage.of("batch", dto, correlationId);

        kafkaTemplate.send(topic, message);
    }
}
