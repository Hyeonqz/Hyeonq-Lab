package io.github.springreactivelab.domain.repository;

import io.github.springreactivelab.domain.entity.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EventRepository extends ReactiveCrudRepository<Event, Long> {

}
