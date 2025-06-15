package org.hyeonqz.springlab.event_best_example.events;

import org.hyeonqz.springlab.event_best_example.dto.CreatePaymentDailyTotalAmount;
import org.springframework.context.ApplicationEvent;

public class CreatePaymentDailyTotalAmountEvent extends ApplicationEvent {
    private final CreatePaymentDailyTotalAmount createPaymentDailyTotalAmount;

    public CreatePaymentDailyTotalAmountEvent (Object source,
        CreatePaymentDailyTotalAmount createPaymentDailyTotalAmount) {
        super(source);
        this.createPaymentDailyTotalAmount = createPaymentDailyTotalAmount;
    }

}
