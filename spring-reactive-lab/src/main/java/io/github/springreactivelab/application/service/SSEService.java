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
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class SSEService {
    // replay().latest(): 구독자가 0명이 되어도 sink가 cancelled 되지 않음 (최신 1개 버퍼)
    // multicast()는 마지막 구독자 해제 시 sink가 영구 취소됨 (FAIL_CANCELLED)
    private final Sinks.Many<Event> sink = Sinks.many().replay().latest();
    private final EventRepository eventRepository;
    private static final AtomicInteger subscriberCount = new AtomicInteger(0);


    public Flux<ServerSentEvent<Event>> subscribe() {
        log.info("SSE 구독 시작 - thread={}", Thread.currentThread().getName());
        return sink.asFlux()
                .doOnSubscribe(s -> {
                    int count = subscriberCount.incrementAndGet();
                    log.info("SSE 클라이언트 연결됨 - 현재 구독자 수: {}", count);
                })
                .doFinally(signalType -> {
                    int count = subscriberCount.decrementAndGet();
                    log.info("SSE 클라이언트 연결 종료 - 종료 사유: {}, 현재 구독자 수: {}", signalType, count);
                })
                .map(event -> {
                    log.debug("SSE 이벤트 전송 - id={}, type={}, payload={}", event.getId(), event.getType(), event.getPayload());
                    return ServerSentEvent.builder(event)
                            .id(String.valueOf(event.getId()))
                            .event(event.getType())
                            .build();
                })
                .mergeWith(heartbeat());
    }

    public Mono<Event> publish(Event event) {
        log.info("이벤트 발행 요청 - type={}, payload={}", event.getType(), event.getPayload());
        return eventRepository.save(event)
                .doOnNext(savedEvent -> {
                    log.info("이벤트 DB 저장 완료 - id={}, type={}", savedEvent.getId(), savedEvent.getType());
                    Sinks.EmitResult result = sink.tryEmitNext(savedEvent);
                    if (result.isFailure()) {
                        log.error("SSE emit 실패 - id={}, result={}", savedEvent.getId(), result);
                    } else {
                        log.info("SSE emit 성공 - id={}, 구독자에게 전달됨", savedEvent.getId());
                    }
                })
                .doOnError(e -> log.error("이벤트 발행 실패 - type={}, error={}", event.getType(), e.getMessage()));
    }

    private Flux<ServerSentEvent<Event>> heartbeat() {
        return Flux.interval(Duration.ofSeconds(15))
                .map(i -> ServerSentEvent.<Event>builder()
                        .comment("ping")
                        .build());
    }
}
