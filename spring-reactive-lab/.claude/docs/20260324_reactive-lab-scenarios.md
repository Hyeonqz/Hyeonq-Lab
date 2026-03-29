# Spring Reactive Lab — 연구 시나리오 실행 계획

> 작성일: 2026-03-24
> 목표: H2 DB 설정 + SSE / Netty / 대용량 처리 3개 시나리오 구현

---

---

## Phase 0: application.yaml 설정

**파일**: `src/main/resources/application.yaml`

```yaml
spring:
  application:
    name: spring-reactive-lab

  datasource:
    url: jdbc:h2:mem:reactivelab;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health, metrics, env

logging:
  level:
    reactor.netty: DEBUG
```

**주의**: WebFlux 환경에서 H2 Console은 서블릿 기반이라 동작하지 않을 수 있다.
안 된다면 `spring.datasource.url`을 `jdbc:h2:tcp://localhost/~/reactivelab` (TCP 서버 모드)로 변경한다.

---

## Phase 1: 공통 인프라

```
src/main/java/io/github/springreactivelab/
├── common/
│   └── ApiResponse.java          # 공통 응답 래퍼
└── config/
    ├── SchedulerConfig.java       # JPA 블로킹 격리용 Scheduler Bean
    └── GlobalExceptionHandler.java
```

### ApiResponse.java

```java
@Getter
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) { ... }
    public static <T> ApiResponse<T> error(String message) { ... }
}
```

### SchedulerConfig.java

```java
@Configuration
public class SchedulerConfig {
    @Bean
    public Scheduler jpaScheduler() {
        return Schedulers.newBoundedElastic(
            Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,  // thread cap
            Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
            "jpa-worker"
        );
    }
}
```

---

## Phase 2: 시나리오 1 — SSE (Server-Sent Events)

### 목표
`Flux` 기반 SSE 스트림 구현. 여러 클라이언트가 동시에 구독하는 핫 스트림 패턴 연구.

### 패키지 구조

```
scenario1_sse/
├── entity/Event.java
├── repository/EventRepository.java
├── service/SseService.java
└── controller/SseController.java
```

### Entity

```java
@Entity
public class Event {
    @Id @GeneratedValue
    private Long id;
    private String type;
    private String payload;
    private LocalDateTime createdAt;
}
```

### SseService — 핵심 포인트

```java
@Service
public class SseService {
    // 모든 구독자에게 브로드캐스트하는 핫 스트림
    private final Sinks.Many<Event> sink = Sinks.many().multicast().onBackpressureBuffer();

    public Flux<ServerSentEvent<Event>> subscribe() {
        return sink.asFlux()
            .map(event -> ServerSentEvent.builder(event)
                .id(String.valueOf(event.getId()))
                .event(event.getType())
                .build())
            .mergeWith(heartbeat());  // 연결 유지용 ping
    }

    private Flux<ServerSentEvent<Event>> heartbeat() {
        return Flux.interval(Duration.ofSeconds(15))
            .map(i -> ServerSentEvent.<Event>builder()
                .comment("ping")
                .build());
    }

    public Mono<Event> publish(Event event) {
        return Mono.fromCallable(() -> repository.save(event))
            .subscribeOn(jpaScheduler)
            .doOnNext(sink::tryEmitNext);
    }
}
```

**연구 포인트**:
- `Sinks.many().multicast()` — 구독 이후 발행된 이벤트만 수신
- `Sinks.many().replay(n)` — 구독 시 최근 n개 이벤트도 재생

### Controller 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/sse/subscribe` | SSE 스트림 구독 (`text/event-stream`) |
| POST | `/api/sse/publish` | 이벤트 발행 |
| GET | `/api/sse/history` | 저장된 이벤트 조회 |

```java
@GetMapping(value = "/api/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<Event>> subscribe() {
    return sseService.subscribe();
}
```

### 수동 테스트

```bash
# 구독 (터미널 1)
curl -N http://localhost:15867/api/sse/subscribe

# 이벤트 발행 (터미널 2)
curl -X POST http://localhost:15867/api/sse/publish \
  -H "Content-Type: application/json" \
  -d '{"type":"order","payload":"order-123"}'
```

---

## Phase 3: 시나리오 2 — Netty Thread Model 연구

### 목표
Netty의 EventLoop 동작 방식 이해. worker thread 수 조정에 따른 성능 변화 관찰.

### 패키지 구조

```
scenario2_netty/
├── config/NettyServerConfig.java
└── controller/NettyInspectionController.java
```

### NettyServerConfig — 핵심 포인트

```java
@Configuration
public class NettyServerConfig implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    @Value("${netty.worker-count:0}")  // 0 = Netty 기본값 (CPU 코어 * 2)
    private int workerCount;

    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        factory.addServerCustomizers(httpServer ->
            httpServer.runOn(LoopResources.create("lab-http", 1, workerCount == 0
                ? Runtime.getRuntime().availableProcessors() * 2
                : workerCount, true))
        );
    }
}
```

### NettyInspectionController 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| GET | `/api/netty/thread-info` | 요청 처리 스레드명, EventLoop 정보 반환 |
| GET | `/api/netty/blocking-test` | 블로킹 호출 후 스레드 전환 관찰 |
| GET | `/api/netty/non-blocking-test` | 순수 논블로킹 처리의 스레드 관찰 |

```java
@GetMapping("/api/netty/thread-info")
public Mono<Map<String, Object>> threadInfo() {
    return Mono.just(Map.of(
        "thread", Thread.currentThread().getName(),
        "isVirtual", Thread.currentThread().isVirtual()  // Java 21+
    ));
}

@GetMapping("/api/netty/blocking-test")
public Mono<Map<String, Object>> blockingTest() {
    String before = Thread.currentThread().getName();
    return Mono.fromCallable(() -> {
        Thread.sleep(100);  // 의도적 블로킹
        return Thread.currentThread().getName();
    })
    .subscribeOn(Schedulers.boundedElastic())
    .map(after -> Map.of("before", before, "after", after));
}
```

### application-scenario2.yaml

```yaml
netty:
  worker-count: 4  # 1, 2, 4, 8 으로 바꿔가며 실험

logging:
  level:
    reactor.netty: DEBUG
    reactor.netty.http.server: DEBUG

management:
  metrics:
    enable:
      reactor.netty: true
```

### 성능 비교 실험 (wrk 설치 필요)

```bash
# worker-count 를 1, 2, 4, 8 로 바꿔가며 실행
wrk -t4 -c100 -d30s http://localhost:8080/api/netty/thread-info
wrk -t4 -c100 -d30s http://localhost:8080/api/netty/blocking-test
wrk -t4 -c100 -d30s http://localhost:8080/api/netty/non-blocking-test
```

---

## Phase 4: 시나리오 3 — 동시 대용량 요청 처리 (Backpressure)

### 목표
Reactor의 Backpressure 전략 이해. 대용량 데이터 스트림 처리 시 안정성 확보.

### 패키지 구조

```
scenario3_backpressure/
├── entity/Order.java
├── repository/OrderRepository.java
├── service/BackpressureService.java
└── controller/BackpressureController.java
```

### Entity

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    private Long id;
    private String itemName;
    private int quantity;
    private String status;
    private LocalDateTime processedAt;
}
```

### BackpressureService — 핵심 포인트

```java
@Service
public class BackpressureService {

    // 전략 1: Buffer — 처리 못한 것을 큐에 쌓음
    public Flux<Order> processWithBuffer(int count) {
        return Flux.range(1, count)
            .map(i -> new Order("item-" + i, 1))
            .onBackpressureBuffer(1000)
            .flatMap(order -> saveOrder(order), 10);  // concurrency = 10
    }

    // 전략 2: Drop — 처리 못한 것을 버림
    public Flux<Order> processWithDrop(int count) {
        return Flux.range(1, count)
            .map(i -> new Order("item-" + i, 1))
            .onBackpressureDrop(dropped -> log.warn("Dropped: {}", dropped))
            .flatMap(order -> saveOrder(order), 10);
    }

    // 전략 3: Latest — 가장 최근 것만 유지
    public Flux<Order> processWithLatest(int count) {
        return Flux.range(1, count)
            .map(i -> new Order("item-" + i, 1))
            .onBackpressureLatest()
            .flatMap(order -> saveOrder(order), 10);
    }

    // 배치 저장 (효율적)
    public Flux<Order> processInBatch(int count, int batchSize) {
        return Flux.range(1, count)
            .map(i -> new Order("item-" + i, 1))
            .buffer(batchSize)
            .flatMap(batch ->
                Mono.fromCallable(() -> repository.saveAll(batch))
                    .subscribeOn(jpaScheduler)
                    .flatMapMany(Flux::fromIterable),
                4  // 배치 4개 동시 처리
            );
    }

    private Mono<Order> saveOrder(Order order) {
        return Mono.fromCallable(() -> repository.save(order))
            .subscribeOn(jpaScheduler);
    }
}
```

**연구 포인트**:
- `flatMap(fn, concurrency)` — 동시 처리 수 제한
- `buffer(n)` + `flatMap` — 배치 처리로 DB INSERT 최적화
- `publishOn` vs `subscribeOn` 의 스레드 전환 위치 차이

### Controller 엔드포인트

| Method | Path | 설명 |
|---|---|---|
| POST | `/api/bp/generate/{count}` | count건 주문 생성/저장 |
| GET | `/api/bp/stream/{count}` | count건 스트리밍 응답 (NDJSON) |
| POST | `/api/bp/strategy/{type}` | buffer/drop/latest 전략 지정 처리 |

```java
// 스트리밍 응답 예시
@GetMapping(value = "/api/bp/stream/{count}", produces = MediaType.APPLICATION_NDJSON_VALUE)
public Flux<Order> stream(@PathVariable int count) {
    return service.processInBatch(count, 50);
}
```

### application-scenario3.yaml

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50       # 배치 INSERT 최적화
        order_inserts: true
        order_updates: true

backpressure:
  concurrency: 10        # flatMap 동시 처리 수
  buffer-size: 1000      # onBackpressureBuffer 크기
  batch-size: 50         # buffer() 사이즈
```

### 부하 테스트

```bash
# k6 설치 후 (https://k6.io)
k6 run --vus 100 --duration 30s script.js

# 또는 wrk
wrk -t8 -c200 -d30s -s post.lua http://localhost:8080/api/bp/generate/1000
```

---

## 구현 순서

```
Phase 0  application.yaml ................. 먼저 설정
Phase 1  ApiResponse + SchedulerConfig .... 공통 기반
         ↓
Phase 2  시나리오 1 SSE
Phase 3  시나리오 2 Netty    ← 독립적이므로 어떤 순서든 무방
Phase 4  시나리오 3 Backpressure
```

---

## 생성 파일 목록

```
src/main/resources/
├── application.yaml                                    (수정)
├── application-scenario1.yaml
├── application-scenario2.yaml
└── application-scenario3.yaml

src/main/java/io/github/springreactivelab/
├── common/ApiResponse.java
├── config/
│   ├── SchedulerConfig.java
│   └── GlobalExceptionHandler.java
├── scenario1_sse/
│   ├── entity/Event.java
│   ├── repository/EventRepository.java
│   ├── service/SseService.java
│   └── controller/SseController.java
├── scenario2_netty/
│   ├── config/NettyServerConfig.java
│   └── controller/NettyInspectionController.java
└── scenario3_backpressure/
    ├── entity/Order.java
    ├── repository/OrderRepository.java
    ├── service/BackpressureService.java
    └── controller/BackpressureController.java
```
