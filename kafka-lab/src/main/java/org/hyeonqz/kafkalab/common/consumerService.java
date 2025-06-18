package org.hyeonqz.kafkalab.common;

import org.springframework.kafka.support.Acknowledgment;

public interface consumerService<T> {
    void consumeMessage(T data, Acknowledgment acknowledgment);
}
