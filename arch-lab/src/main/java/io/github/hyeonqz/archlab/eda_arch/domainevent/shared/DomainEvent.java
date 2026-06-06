package io.github.hyeonqz.archlab.eda_arch.domainevent.shared;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public abstract class DomainEvent {
    private final LocalDateTime occurredAt = LocalDateTime.now();

}
