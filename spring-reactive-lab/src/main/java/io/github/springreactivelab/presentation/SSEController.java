package io.github.springreactivelab.presentation;

import io.github.springreactivelab.application.service.SSEService;
import io.github.springreactivelab.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequestMapping("/api/sse")
@RestController
@RequiredArgsConstructor
public class SSEController {
    private final SSEService sseService;

    @GetMapping("/subscribe")
    public ResponseEntity<Flux<ServerSentEvent<Event>>> subscribe() {
        log.info("[API] GET /api/sse/subscribe 요청");
        return ResponseEntity.ok(sseService.subscribe());
    }

    @PostMapping("/publish")
    public ResponseEntity<Mono<Event>> publish(@RequestBody Event event) {
        log.info("[API] POST /api/sse/publish 요청 - type={}, payload={}", event.getType(), event.getPayload());
        return ResponseEntity.ok(sseService.publish(event));
    }

    @GetMapping("/history")
    public ResponseEntity<Flux<ServerSentEvent<Event>>> history() {
        log.info("[API] GET /api/sse/history 요청");
        return null;
        // return ResponseEntity.ok(sseService.history());
    }

}
