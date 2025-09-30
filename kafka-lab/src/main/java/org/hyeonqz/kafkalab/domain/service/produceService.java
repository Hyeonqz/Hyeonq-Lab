package org.hyeonqz.kafkalab.domain.service;

public interface produceService<T> {
    void produceMessage(T data);
}
