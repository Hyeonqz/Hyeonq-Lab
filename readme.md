# Hyeonkyu Lab

평소 궁금한 내용을 실험해보는 종합 학습 레포지토리입니다.

## 프로젝트 개요

- **언어**: Java 21
- **빌드 도구**: Gradle
- **주요 프레임워크**: Spring Boot 3.4.x ~ 3.5.x

## 모듈 구성

### 1. [java-lab](./java-lab)
Java 여러 개념 및 JDK 라이브러리들 실습을 진행합니다.
- Sealed Classes & Pattern Matching
- Thread Safety & Concurrency
- Data Structures 구현
- Java 언어 기능 실험

### 2. [spring-lab](./spring-lab) - Port 9300
일반적인 Spring 환경에서 궁금했던 부분을 실습을 진행합니다.
- 예외 처리 아키텍처 (ErrorCode, GlobalExceptionHandler)
- 이벤트 기반 아키텍처 (Spring Events)
- 비동기 처리 (TaskExecutor, @Async)
- Spring AI 통합 (OpenAI)
- Actuator & Prometheus 메트릭
- MDC 기반 요청별 로깅
- HikariCP 커넥션 풀 모니터링

### 3. [kafka-lab](./kafka-lab) - Port 9200
Kafka 통신과 관련된 궁금했던 부분을 실험해봅니다.
- Producer/Consumer 패턴 구현
- Kafka Admin API 활용
- 배치 처리 (max-poll-records: 6000)
- 감사 로그 시스템 (audit logging)
- KRaft & Zookeeper 모드 지원
- JMH 성능 벤치마킹

### 4. [redis-lab](./redis-lab) - Port 9400
Redis 캐싱 및 인메모리 데이터 관리를 학습합니다.
- Spring Data Redis (Lettuce 클라이언트)
- Redisson 통합
- POS 터미널 정산 시스템
- RDB 동기화 패턴
- 기본 개념 및 참조: [Redis Gate](http://redisgate.kr/redis/clients/spring_strings.php)

### 5. [architecture-lab](./architecture-lab)
현대적인 소프트웨어 아키텍처 패턴을 탐구합니다.
- Clean Architecture (2가지 예제)
- Hexagonal Architecture (Ports & Adapters)
- Layered Architecture (전통적 3계층)
- Domain-Driven Design (DDD)

### 6. [design-pattern-lab](./design-pattern-lab)
GoF 디자인 패턴을 알아보며, 실무에 적용해볼만한 패턴을 연구합니다.
- 생성 패턴: Builder, Factory, Singleton, Prototype
- 구조 패턴: Adapter, Decorator, Facade, Proxy
- 행위 패턴: Observer, Strategy, Command, State

### 7. [spring-cloud-lab](./spring-cloud-lab) - Port 8300
마이크로서비스 아키텍처와 분산 시스템을 학습합니다.
- Spring Cloud 2024.0.1
- Eureka Server & Client (서비스 디스커버리)
- HashiCorp Vault (시크릿 관리)
- 분산 시스템 패턴

## 기술 스택

### 공통 기술
- Java 21
- Gradle
- JUnit 5 (Jupiter)
- Lombok

### Spring 생태계
- Spring Boot 3.x
- Spring Web / MVC
- Spring Data JPA
- Spring AOP
- Spring Boot Actuator
- Spring Validation
- Spring AI

### 데이터 저장소
- MySQL 8.0
- H2 Database
- Redis
- HikariCP

### 메시징 & 이벤트
- Apache Kafka
- Spring Kafka
- Spring Events

### 마이크로서비스
- Spring Cloud Eureka
- HashiCorp Vault

### 모니터링 & 로깅
- Prometheus
- SLF4J with MDC
- Spring Boot Actuator

## 인프라 구성

| 서비스 | 포트 | 용도 | 데이터베이스 |
|--------|------|------|-------------|
| spring-lab | 9300 | 스프링 핵심 학습 | H2 |
| kafka-lab | 9200 | 메시지 스트리밍 | MySQL (kafka_lab) |
| redis-lab | 9400 | 캐싱 & NoSQL | MySQL (redis_lab) |
| spring-cloud-lab | 8300 | 마이크로서비스 | H2 (vault) |
| Kafka Brokers | 9092-9094 | 메시지 브로커 | - |
| Redis | 6379 | 인메모리 캐시 | - |
| MySQL | 3306 | 관계형 DB | - |
| Vault | 8200 | 시크릿 관리 | - |

## 시작하기

### 전체 프로젝트 빌드
```bash
./gradlew build
```

### 특정 모듈 빌드
```bash
./gradlew :spring-lab:build
./gradlew :kafka-lab:build
```

### 애플리케이션 실행
```bash
# Spring Lab 실행
./gradlew :spring-lab:bootRun

# Kafka Lab 실행 (Docker Compose로 Kafka 실행 필요)
cd kafka-lab/docker-compose/kraft
docker-compose up -d
./gradlew :kafka-lab:bootRun
```

## 프로젝트 구조
```
hyeonkyu-lab/
├── java-lab/              # 순수 Java 학습
├── spring-lab/            # Spring Framework 핵심 기능
├── kafka-lab/             # Kafka 메시징 & 이벤트 스트리밍
├── redis-lab/             # Redis 캐싱 & NoSQL
├── architecture-lab/      # 소프트웨어 아키텍처 패턴
├── design-pattern-lab/    # GoF 디자인 패턴
└── spring-cloud-lab/      # 마이크로서비스 & 분산 시스템
```

## 학습 주제

### 핵심 Java
- Sealed Classes & Pattern Matching
- Thread Safety & Concurrency
- Java Data Structures
- Modern Java Features

### Spring 핵심 개념
- 전역 예외 처리 전략
- 이벤트 기반 아키텍처
- AOP (Aspect-Oriented Programming)
- 비동기 처리 & 멀티스레딩
- 트랜잭션 관리

### 분산 시스템
- 메시지 큐 (Apache Kafka)
- 캐싱 전략 (Redis)
- 서비스 디스커버리 (Eureka)
- 시크릿 관리 (HashiCorp Vault)

### 아키텍처 & 디자인
- Clean Architecture
- Hexagonal Architecture
- Domain-Driven Design
- Gang of Four Design Patterns

## 최근 변경사항

- DataSourceConfig를 application.yml로 마이그레이션
- Payment AOP 예제 추가
- TaskExecutor 및 비동기 스레드 학습
- MDC 기반 요청별 로깅 구현
- Kafka 감사 로그 Producer/Consumer 추가
- Spring-tcp-lab 모듈 제거

## 참고 자료

- [Redis Gate - Spring Redis 가이드](http://redisgate.kr/redis/clients/spring_strings.php)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Apache Kafka 문서](https://kafka.apache.org/documentation/)
- [Spring Cloud 공식 문서](https://spring.io/projects/spring-cloud)