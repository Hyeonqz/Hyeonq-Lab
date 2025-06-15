package org.hyeonqz.springlab.event_best_example.publisher;

public interface EventPublisher<T> {
    void publish(T event);
}
