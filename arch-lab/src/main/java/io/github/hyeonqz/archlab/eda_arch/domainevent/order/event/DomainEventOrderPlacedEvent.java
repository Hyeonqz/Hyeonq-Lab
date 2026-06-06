package io.github.hyeonqz.archlab.eda_arch.domainevent.order.event;


import io.github.hyeonqz.archlab.eda_arch.domainevent.shared.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DomainEventOrderPlacedEvent extends DomainEvent {
    private final Long orderId;
    private final String customerId;
    private final Long amount;

}
