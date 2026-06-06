package io.github.hyeonqz.archlab.eda_arch.applicationevent.payment.event;

public record AppEventPaymentCompletedEvent(Long orderId, Long amount, String status) {}
