# Kafka Lab

Apache Kafka를 활용한 메시지 큐 및 이벤트 스트리밍을 학습하는 모듈입니다.

## 개요

Spring Kafka를 사용하여 Producer/Consumer 패턴, 배치 처리, 감사 로깅 시스템을 구현하며, KRaft와 Zookeeper 모드를 모두 지원하는 Kafka 클러스터를 구축합니다.

## 기술 스택

- Java 21
- Spring Boot 3.5.6
- Spring Kafka
- Apache Kafka
- MySQL 8.0
- Spring Data JPA
- JMH (Java Microbenchmark Harness)
- Docker & Docker Compose
- Lombok

## 포트 구성

- **애플리케이션**: 9200
- **Kafka Brokers**: 9092, 9093, 9094
- **MySQL**: 3306
- **Zookeeper**: 2181 (Zookeeper 모드)

## 주요 학습 주제

### 1. Kafka Producer/Consumer 패턴

#### Producer (`application/producer/`)
메시지를 Kafka 토픽에 발행하는 생산자 구현

```java
@Service
public class LogProducer {
    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    public void sendLog(LogRequestDto dto) {
        KafkaMessage message = new KafkaMessage(dto);
        kafkaTemplate.send("toy-audit", message);
    }
}
```

#### Consumer (`application/consumer/`)
Kafka 토픽에서 메시지를 소비하는 소비자 구현

```java
@Component
public class LogConsumer {
    @KafkaListener(topics = "toy-audit", groupId = "audit-group")
    public void consume(KafkaMessage message) {
        // 메시지 처리 로직
        log.info("Received message: {}", message);
    }
}
```

### 2. Kafka 설정

#### Producer 설정 (`config/producer/KafkaProducerConfig`)
```java
@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, KafkaMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
```

#### Consumer 설정 (`config/consumer/KafkaConsumerConfig`)
```java
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, KafkaMessage> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "audit-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 6000); // 배치 처리
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
```

### 3. 배치 처리

대용량 메시지 처리를 위한 배치 설정

#### 설정 파라미터
- `max.poll.records`: 6000 - 한 번에 가져올 최대 레코드 수
- `fetch.min.bytes`: 최소 페치 크기
- `fetch.max.wait.ms`: 최대 대기 시간

#### 장점
- 처리량 향상
- 네트워크 오버헤드 감소
- 효율적인 리소스 활용

### 4. Kafka Admin API (`config/KafkaAdminConfig`)

토픽 생성 및 관리

```java
@Configuration
public class KafkaAdminConfig {
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic auditTopic() {
        return TopicBuilder.name("toy-audit")
            .partitions(3)
            .replicas(2)
            .build();
    }
}
```

### 5. 감사 로그 시스템

#### 데이터 흐름
1. 애플리케이션에서 이벤트 발생
2. Producer가 Kafka 토픽에 메시지 발행
3. Consumer가 메시지 소비
4. MySQL에 감사 로그 저장

#### 엔티티 (`domain/entity/`)
```java
@Entity
@Table(name = "request_logs")
public class RequestLog {
    @Id
    private Long id;
    private String userId;
    private String action;
    private LocalDateTime timestamp;
    private String details;
}
```

### 6. Docker Compose 구성

#### KRaft 모드 (`docker-compose/kraft/`)
Zookeeper 없이 Kafka만으로 클러스터 구성

```yaml
version: '3'
services:
  kafka-1:
    image: apache/kafka:latest
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
    ports:
      - "9092:9092"
```

#### Zookeeper 모드 (`docker-compose/zookeeper/`)
전통적인 Zookeeper + Kafka 구성

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
```

### 7. JMH 성능 벤치마킹

Java Microbenchmark Harness를 사용한 성능 측정

#### 측정 항목
- Producer 처리량
- Consumer 처리량
- 직렬화/역직렬화 성능
- 배치 크기별 성능 비교

```java
@Benchmark
@BenchmarkMode(Mode.Throughput)
public void measureProducerThroughput() {
    logProducer.sendLog(createTestLog());
}
```

## 프로젝트 구조

```
kafka-lab/
├── src/
│   ├── main/
│   │   ├── java/org/hyeonqz/kafkalab/
│   │   │   ├── config/
│   │   │   │   ├── producer/           # Producer 설정
│   │   │   │   ├── consumer/           # Consumer 설정
│   │   │   │   └── KafkaAdminConfig.java
│   │   │   ├── application/
│   │   │   │   ├── producer/           # LogProducer
│   │   │   │   └── consumer/           # LogConsumer
│   │   │   ├── domain/
│   │   │   │   ├── dto/                # DTO
│   │   │   │   ├── entity/             # JPA Entity
│   │   │   │   └── service/            # 비즈니스 로직
│   │   │   ├── shared/
│   │   │   │   └── message/            # KafkaMessage
│   │   │   ├── infra/
│   │   │   │   ├── repository/         # JPA Repository
│   │   │   │   └── adapter/
│   │   │   └── presentation/           # Controller
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── docker-compose/
│   ├── kraft/                          # KRaft 모드
│   │   └── docker-compose.yml
│   └── zookeeper/                      # Zookeeper 모드
│       └── docker-compose.yml
├── docs/
│   └── Kafka docs.md                   # Kafka 문서
└── build.gradle
```

## 실행 방법

### 1. Kafka 클러스터 시작

#### KRaft 모드 (권장)
```bash
cd docker-compose/kraft
docker-compose up -d
```

#### Zookeeper 모드
```bash
cd docker-compose/zookeeper
docker-compose up -d
```

### 2. MySQL 시작
```bash
docker run -d \
  --name mysql-kafka \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=kafka_lab \
  -p 3306:3306 \
  mysql:8.0
```

### 3. 애플리케이션 실행
```bash
./gradlew :kafka-lab:bootRun
```

### 4. 빌드 & 테스트
```bash
# 빌드
./gradlew :kafka-lab:build

# 테스트
./gradlew :kafka-lab:test

# JMH 벤치마크 실행
./gradlew :kafka-lab:jmh
```

## Kafka 토픽 관리

### 토픽 목록 확인
```bash
docker exec -it kafka-1 kafka-topics --bootstrap-server localhost:9092 --list
```

### 토픽 생성
```bash
docker exec -it kafka-1 kafka-topics \
  --bootstrap-server localhost:9092 \
  --create \
  --topic toy-audit \
  --partitions 3 \
  --replication-factor 2
```

### 토픽 상세 정보
```bash
docker exec -it kafka-1 kafka-topics \
  --bootstrap-server localhost:9092 \
  --describe \
  --topic toy-audit
```

### 메시지 확인 (Consumer Console)
```bash
docker exec -it kafka-1 kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic toy-audit \
  --from-beginning
```

### 메시지 발행 (Producer Console)
```bash
docker exec -it kafka-1 kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic toy-audit
```

## 주요 토픽

| 토픽 이름 | 파티션 | 복제본 | 용도 |
|----------|--------|--------|------|
| toy-audit | 3 | 2 | 감사 로그 |
| realtime-transaction-batch | 3 | 2 | 실시간 트랜잭션 |
| payment-log | 3 | 2 | 결제 로그 |

## 설정 파일

### application.yml
```yaml
server:
  port: 9200
  shutdown: graceful

spring:
  application:
    name: kafka-lab
  datasource:
    url: jdbc:mysql://localhost:3306/kafka_lab
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  kafka:
    bootstrap-servers: localhost:9092,localhost:9093,localhost:9094
    consumer:
      group-id: audit-group
      auto-offset-reset: earliest
      max-poll-records: 6000
      enable-auto-commit: false
    producer:
      acks: all
      retries: 3
```

## 성능 최적화

### Producer 최적화
- `acks=all`: 모든 복제본 확인 (신뢰성)
- `compression.type=snappy`: 압축으로 네트워크 대역폭 절약
- `batch.size`: 배치 크기 조정
- `linger.ms`: 메시지 대기 시간

### Consumer 최적화
- `max.poll.records=6000`: 배치 처리로 처리량 향상
- `fetch.min.bytes`: 최소 페치 크기 설정
- `enable.auto.commit=false`: 수동 커밋으로 정확성 보장

### 파티션 전략
- 파티션 수 = Consumer 수 (최적 병렬 처리)
- 키 기반 파티셔닝으로 순서 보장
- 파티션 리밸런싱 고려

## 모니터링

### Kafka 클러스터 상태
```bash
docker exec -it kafka-1 kafka-broker-api-versions --bootstrap-server localhost:9092
```

### Consumer Group 상태
```bash
docker exec -it kafka-1 kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group audit-group
```

### Lag 모니터링
```bash
docker exec -it kafka-1 kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group audit-group \
  --members
```

## 학습 포인트

### Kafka 핵심 개념
- **Broker**: 메시지를 저장하는 서버
- **Topic**: 메시지 카테고리
- **Partition**: 토픽의 물리적 분할
- **Producer**: 메시지 발행자
- **Consumer**: 메시지 소비자
- **Consumer Group**: 협력하는 Consumer 그룹
- **Offset**: 메시지 위치 추적

### 보장 수준
- **At most once**: 최대 1회 (메시지 손실 가능)
- **At least once**: 최소 1회 (중복 가능)
- **Exactly once**: 정확히 1회 (Kafka Transactions)

### KRaft vs Zookeeper
- **KRaft**: Kafka 3.0+, Zookeeper 제거, 더 간단한 운영
- **Zookeeper**: 전통적 방식, 안정성 검증됨

## 참고 자료

- [Apache Kafka 공식 문서](https://kafka.apache.org/documentation/)
- [Spring Kafka 문서](https://spring.io/projects/spring-kafka)
- [Confluent Platform](https://docs.confluent.io/)
- [Kafka: The Definitive Guide](https://www.confluent.io/resources/kafka-the-definitive-guide/)
- [./docs/Kafka docs.md](./docs/Kafka%20docs.md) - 프로젝트 내 Kafka 문서

## 트러블슈팅

### Kafka 연결 실패
```bash
# Kafka 상태 확인
docker ps | grep kafka

# 로그 확인
docker logs kafka-1
```

### Consumer Lag 증가
- Consumer 수 증가 (파티션 수만큼)
- 배치 크기 조정
- 처리 로직 최적화

### 메시지 손실
- `acks=all` 설정
- `min.insync.replicas` 설정
- 적절한 복제본 수 (최소 2개)

## 최근 변경사항

- Kafka 감사 로그 Producer/Consumer 추가
- 배치 처리 설정 (max-poll-records: 6000)
- JMH 성능 벤치마킹 도구 추가
- KRaft 모드 Docker Compose 설정 추가
- MySQL 연동 감사 로그 저장
