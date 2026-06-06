package org.hyeonqz.architecturelab.eda.domainevent.shared;

import java.time.LocalDateTime;

public abstract class DomainEvent {
    private final LocalDateTime occurredAt = LocalDateTime.now();

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
