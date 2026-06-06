package org.hyeonqz.architecturelab.eda.domainevent.payment.event;

import org.hyeonqz.architecturelab.eda.domainevent.shared.DomainEvent;

public class DomainEventPaymentCompletedEvent extends DomainEvent {
    private final Long paymentId;
    private final Long orderId;
    private final Long amount;

    public DomainEventPaymentCompletedEvent(Long paymentId, Long orderId, Long amount) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public Long getPaymentId() { return paymentId; }
    public Long getOrderId() { return orderId; }
    public Long getAmount() { return amount; }
}
