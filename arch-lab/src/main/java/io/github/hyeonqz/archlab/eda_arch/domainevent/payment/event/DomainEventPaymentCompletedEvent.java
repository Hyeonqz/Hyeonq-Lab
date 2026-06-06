package io.github.hyeonqz.archlab.eda_arch.domainevent.payment.event;

import io.github.hyeonqz.archlab.eda_arch.domainevent.shared.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DomainEventPaymentCompletedEvent extends DomainEvent {
    private final Long paymentId;
    private final Long orderId;
    private final Long amount;

}
