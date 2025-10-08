package org.hyeonqz.kafkalab.domain.service;

public interface ProduceService<T> {
    void produceMessage(T data);
}
