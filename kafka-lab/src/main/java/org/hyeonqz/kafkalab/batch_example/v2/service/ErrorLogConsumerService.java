package org.hyeonqz.kafkalab.batch_example.v2.service;

import org.hyeonqz.kafkalab.common.consumerService;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ErrorLogConsumerService implements consumerService<Object> {

    @Override
    public void consumeMessage (Object data, Acknowledgment acknowledgment) {

    }

}
