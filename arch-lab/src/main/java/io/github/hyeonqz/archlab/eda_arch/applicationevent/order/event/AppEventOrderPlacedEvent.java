package io.github.hyeonqz.archlab.eda_arch.applicationevent.order.event;

public record AppEventOrderPlacedEvent(Long orderId, String customerId, Long amount) {}
