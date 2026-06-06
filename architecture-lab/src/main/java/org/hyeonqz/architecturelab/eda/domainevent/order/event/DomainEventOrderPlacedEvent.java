package org.hyeonqz.architecturelab.eda.domainevent.order.event;

import org.hyeonqz.architecturelab.eda.domainevent.shared.DomainEvent;

public class DomainEventOrderPlacedEvent extends DomainEvent {
    private final Long orderId;
    private final String customerId;
    private final Long amount;

    public DomainEventOrderPlacedEvent(Long orderId, String customerId, Long amount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
    }

    public Long getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public Long getAmount() { return amount; }
}
