package org.hyeonqz.kafkalab.batch_example.v2.service;

import org.hyeonqz.kafkalab.common.produceService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ErrorLogProduceService implements produceService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produceMessage () {

    }

}
