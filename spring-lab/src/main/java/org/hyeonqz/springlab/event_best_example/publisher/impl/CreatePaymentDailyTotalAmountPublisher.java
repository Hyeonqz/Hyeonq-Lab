package org.hyeonqz.springlab.event_best_example.publisher.impl;

import org.hyeonqz.springlab.event_best_example.dto.CreatePaymentDailyTotalAmount;
import org.hyeonqz.springlab.event_best_example.events.CreatePaymentDailyTotalAmountEvent;
import org.hyeonqz.springlab.event_best_example.publisher.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CreatePaymentDailyTotalAmountPublisher implements EventPublisher<CreatePaymentDailyTotalAmount> {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish (final CreatePaymentDailyTotalAmount event) {
        eventPublisher.publishEvent(new CreatePaymentDailyTotalAmountEvent(this, event));
    }

}
