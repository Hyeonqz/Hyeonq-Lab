package org.hyeonqz.architecturelab.eda.applicationevent.payment.event;

public record AppEventPaymentCompletedEvent(Long orderId, Long amount, String status) {}
