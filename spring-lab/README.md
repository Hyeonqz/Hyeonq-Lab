# Spring Lab

Spring Framework의 핵심 기능과 다양한 실무 패턴을 학습하고 실험하는 모듈입니다.

## 개요

Spring Boot 3.5.6 기반으로 예외 처리, 이벤트 기반 아키텍처, 비동기 처리, AOP, Spring AI 통합 등 실무에서 자주 사용되는 Spring 기능들을 학습합니다.

## 기술 스택

- Java 21
- Spring Boot 3.5.6
- Spring Web / MVC
- Spring Data JPA (Hibernate)
- Spring AOP
- Spring Boot Actuator
- Spring AI (OpenAI Integration)
- Spring Validation
- H2 Database
- HikariCP
- Lombok
- SLF4J with Logback

## 포트 구성

- **애플리케이션**: 9300
- **H2 Console**: `/h2-console`

## 주요 학습 주제

### 1. 예외 처리 아키텍처 (`exceptionEx/`)

전역 예외 처리와 도메인별 에러 코드 관리를 위한 구조화된 접근 방식

#### 디렉토리 구조
```
exceptionEx/
├── application/           # 애플리케이션 로직
│   └── MemberService.java
├── presentation/          # 컨트롤러 계층
│   └── ExceptionController.java
├── shared/                # 공유 예외 처리 컴포넌트
│   ├── ErrorCode.java                 # 에러 코드 인터페이스
│   ├── MemberErrorCode.java           # 회원 도메인 에러 코드
│   ├── MemberException.java           # 도메인 커스텀 예외
│   └── GlobalExceptionHandler.java    # 전역 예외 핸들러
└── infrastructure/        # 인프라 계층
```

#### 주요 특징
- 도메인별 에러 코드 관리 (`ErrorCode` 인터페이스)
- `@RestControllerAdvice`를 통한 전역 예외 처리
- HTTP 상태 코드와 비즈니스 에러 코드 분리
- 일관된 에러 응답 형식

#### 예제 코드
```java
// 에러 코드 정의
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND("M001", "회원을 찾을 수 없습니다"),
    DUPLICATE_EMAIL("M002", "이미 등록된 이메일입니다");

    private final String code;
    private final String message;
}

// 커스텀 예외
public class MemberException extends RuntimeException {
    private final ErrorCode errorCode;
}

// 전역 예외 핸들러
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
        // 에러 응답 생성
    }
}
```

### 2. 이벤트 기반 아키텍처 (`event_example/`, `event_best_example/`)

Spring의 ApplicationEvent를 활용한 느슨한 결합 구현

#### 두 가지 접근 방식
- `event_example/` - 기본 이벤트 패턴
- `event_best_example/` - 베스트 프랙티스 패턴

#### 디렉토리 구조
```
event_best_example/
├── config/                # 이벤트 설정
├── dto/                   # 데이터 전송 객체
├── events/                # 이벤트 정의
├── handler/               # 이벤트 핸들러
└── publisher/             # 이벤트 발행자
```

#### 주요 특징
- `ApplicationEventPublisher`를 통한 이벤트 발행
- `@EventListener`를 통한 비동기 이벤트 처리
- 도메인 간 결합도 감소
- 트랜잭션 경계와 이벤트 처리 분리

#### 예제 코드
```java
// 이벤트 정의
public record OrderCreatedEvent(Long orderId, String customerName) {}

// 이벤트 발행
@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;

    public void createOrder(Order order) {
        // 주문 생성 로직
        eventPublisher.publishEvent(new OrderCreatedEvent(order.getId(), order.getCustomer()));
    }
}

// 이벤트 핸들러
@Component
public class NotificationHandler {
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        // 알림 발송 로직
    }
}
```

### 3. 비동기 처리 (`thread_example/`)

TaskExecutor와 @Async를 활용한 비동기 프로그래밍

#### 디렉토리 구조
```
thread_example/
├── config/                # ThreadPoolTaskExecutor 설정
├── presentation/          # 컨트롤러
└── AsyncExampleService.java
```

#### 주요 개념
- `@Async` 어노테이션을 통한 비동기 메서드
- `ThreadPoolTaskExecutor` 커스텀 설정
- 톰캣 스레드 vs 비동기 스레드 풀
- 비동기 작업의 예외 처리
- CompletableFuture 활용

#### 설정 예제
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        return executor;
    }
}
```

### 4. Spring AI 통합 (`openai/`)

OpenAI API를 활용한 AI 기능 통합

#### 주요 특징
- Spring AI 프레임워크 활용
- ChatGPT API 통합
- 프롬프트 엔지니어링
- API 키 관리 (application-secret.yml)

#### 설정
```yaml
# application-secret.yml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
```

### 5. AOP (Aspect-Oriented Programming)

횡단 관심사를 분리하는 관점 지향 프로그래밍

#### 주요 사용 사례
- 로깅 (메서드 실행 시간, 파라미터 로깅)
- 트랜잭션 관리
- 보안 (권한 체크)
- 캐싱
- 예외 처리

### 6. 로깅 & 모니터링

#### MDC (Mapped Diagnostic Context) - `logging/`
요청별 컨텍스트 정보를 로그에 포함

```java
@Component
public class MDCFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
```

#### Actuator & Prometheus - `actuator/`
- 애플리케이션 메트릭 수집
- 헬스 체크 엔드포인트
- Prometheus 포맷 메트릭 노출

### 7. HikariCP 커넥션 풀 모니터링 (`config/`)

#### HikariPoolMonitor
- 커넥션 풀 상태 모니터링
- 활성/유휴 커넥션 추적
- 커넥션 타임아웃 설정

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 8. REST Client (`config/`)

Spring 6의 새로운 REST Client 활용

#### RestClientConfig
- HTTP 클라이언트 설정
- 요청/응답 인터셉터
- 타임아웃 설정
- 에러 핸들링

```java
@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
            .baseUrl("https://api.example.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .requestInterceptor(new RestClientInterceptor())
            .build();
    }
}
```

### 9. 기타 기능

- **Bean Lifecycle** (`lifecycle/`) - 스프링 빈 생명주기 학습
- **Custom Annotations** (`annotation/`) - 커스텀 어노테이션 생성
- **File Upload** (`multipart/`) - 멀티파트 파일 업로드 처리
- **JSCH** (`jsch/`) - SSH/SFTP 작업
- **Troubleshooting** (`troubleshooting/`) - 디버깅 예제

## 프로젝트 구조

```
spring-lab/
├── src/
│   ├── main/
│   │   ├── java/org/hyeonqz/springlab/
│   │   │   ├── exceptionEx/           # 전역 예외 처리
│   │   │   ├── event_example/         # 기본 이벤트 패턴
│   │   │   ├── event_best_example/    # 이벤트 베스트 프랙티스
│   │   │   ├── thread_example/        # 비동기 처리
│   │   │   ├── config/                # 애플리케이션 설정
│   │   │   ├── openai/                # Spring AI 통합
│   │   │   ├── actuator/              # 메트릭 & 모니터링
│   │   │   ├── logging/               # MDC 로깅
│   │   │   ├── lifecycle/             # Bean 생명주기
│   │   │   ├── annotation/            # 커스텀 어노테이션
│   │   │   ├── jsch/                  # SSH/SFTP
│   │   │   ├── multipart/             # 파일 업로드
│   │   │   └── troubleshooting/       # 트러블슈팅
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-secret.yml
│   │       └── docker-compose.yml
│   └── test/
└── build.gradle
```

## 실행 방법

### 애플리케이션 실행
```bash
# Spring Lab 실행
./gradlew :spring-lab:bootRun

# 특정 프로파일로 실행
./gradlew :spring-lab:bootRun --args='--spring.profiles.active=dev'
```

### 빌드
```bash
./gradlew :spring-lab:build
```

### 테스트
```bash
./gradlew :spring-lab:test
```

## 주요 엔드포인트

- **애플리케이션**: `http://localhost:9300`
- **H2 Console**: `http://localhost:9300/h2-console`
- **Actuator**: `http://localhost:9300/actuator`
- **Health Check**: `http://localhost:9300/actuator/health`
- **Prometheus Metrics**: `http://localhost:9300/actuator/prometheus`

## 설정 파일

### application.yml
```yaml
server:
  port: 9300
  shutdown: graceful

spring:
  application:
    name: spring-lab
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    hikari:
      maximum-pool-size: 10
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

management:
  endpoints:
    web:
      exposure:
        include: health,prometheus,metrics
  metrics:
    export:
      prometheus:
        enabled: true
```

### application-secret.yml
OpenAI API 키 등 민감한 정보 관리

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
```

## 학습 포인트

### 1. 계층형 아키텍처
- Presentation Layer (Controller)
- Application Layer (Service)
- Domain Layer (Entity, Value Object)
- Infrastructure Layer (Repository)

### 2. 예외 처리 전략
- 전역 예외 핸들러로 일관된 에러 응답
- 도메인별 에러 코드 관리
- HTTP 상태 코드와 비즈니스 에러 분리

### 3. 비동기 처리 모범 사례
- 적절한 스레드 풀 크기 설정
- 비동기 작업의 예외 처리
- 톰캣 스레드와 비즈니스 스레드 분리

### 4. 이벤트 기반 설계
- 도메인 간 결합도 감소
- 확장 가능한 아키텍처
- 트랜잭션 경계 관리

## 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Framework Reference](https://docs.spring.io/spring-framework/reference/)
- [Spring Data JPA 문서](https://spring.io/projects/spring-data-jpa)
- [Spring AOP 문서](https://docs.spring.io/spring-framework/reference/core/aop.html)
- [Spring AI 문서](https://spring.io/projects/spring-ai)
- [HikariCP 문서](https://github.com/brettwooldridge/HikariCP)
- [Prometheus 메트릭](https://prometheus.io/docs/introduction/overview/)

## 주요 의존성

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-aop'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.ai:spring-ai-openai-spring-boot-starter'
    implementation 'io.micrometer:micrometer-registry-prometheus'
    runtimeOnly 'com.h2database:h2'
    compileOnly 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

## 최근 변경사항

- DataSourceConfig를 application.yml로 마이그레이션
- Payment AOP 예제 추가
- TaskExecutor 및 비동기 스레드 학습 모듈 추가
- MDC 기반 요청별 로깅 구현
- 예외 처리 구조를 shared 패키지로 리팩토링
- RestClient 설정 및 인터셉터 추가
