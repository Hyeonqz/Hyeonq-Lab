package org.hyeonqz.architecturelab.eda.applicationevent.order.event;

public record AppEventOrderPlacedEvent(Long orderId, String customerId, Long amount) {}
