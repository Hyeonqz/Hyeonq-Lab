package io.github.springreactivelab.presentation;

import io.github.springreactivelab.application.service.SSEService;
import io.github.springreactivelab.domain.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/api/sse")
@RestController
@RequiredArgsConstructor
public class SSEController {
    private final SSEService sseService;

    @GetMapping("/subscribe")
    public ResponseEntity<Flux<ServerSentEvent<Event>>> subscribe() {
        return ResponseEntity.ok(sseService.subscribe());
    }

    @PostMapping("/publish")
    public ResponseEntity<Mono<Event>> publish(Event event) {
        return ResponseEntity.ok(sseService.publish(event));
    }


    @GetMapping("/history")
    public ResponseEntity<Flux<ServerSentEvent<Event>>> history() {
        return null;
        // return ResponseEntity.ok(sseService.history());
    }

}
