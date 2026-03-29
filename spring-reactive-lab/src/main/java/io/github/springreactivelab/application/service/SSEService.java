package io.github.springreactivelab.application.service;

import io.github.springreactivelab.domain.entity.Event;
import io.github.springreactivelab.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SSEService {
    private final Sinks.Many<Event> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final EventRepository eventRepository;


    public Flux<ServerSentEvent<Event>> subscribe() {
        return sink.asFlux()
                .map(event -> ServerSentEvent.builder(event)
                        .id(String.valueOf(event.getId()))
                        .event(event.getType())
                        .build())
                .mergeWith(heartbeat());  // 연결 유지용 ping
    }

    public Mono<Event> publish(Event event) {
        return eventRepository.save(event)
                .doOnNext(savedEvent -> {
                    Sinks.EmitResult result = sink.tryEmitNext(savedEvent);
                    if (result.isFailure())
                        log.error("SSE emit 실패: {}", result);
                });
    }

    private Flux<ServerSentEvent<Event>> heartbeat() {
        return Flux.interval(Duration.ofSeconds(15))
                .map(i -> ServerSentEvent.<Event>builder()
                        .comment("ping")
                        .build());
    }
}
