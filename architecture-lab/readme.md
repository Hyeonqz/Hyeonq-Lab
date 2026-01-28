# Architecture Lab

현대적인 소프트웨어 아키텍처 패턴을 학습하고 실험하는 모듈입니다.

## 개요

Clean Architecture, Hexagonal Architecture, Layered Architecture, DDD 등 다양한 아키텍처 패턴을 실제 코드로 구현하며 각 패턴의 장단점과 적용 방법을 학습합니다.

## 기술 스택

- Java 21
- Spring Boot 3.4.3
- Spring Web / MVC
- Spring Data JPA
- Spring Validation
- MySQL 8.0
- Lombok

## 주요 학습 주제

### 1. Clean Architecture (`clean/`)

"만들면서 배우는 클린 아키텍처" 책을 기반으로 구현

#### 핵심 원칙
- 의존성 규칙 (Dependency Rule)
- 계층 분리 (Layer Separation)
- 인터페이스를 통한 의존성 역전 (DIP)
- 비즈니스 로직의 독립성

#### 계층 구조
```
┌─────────────────────────────────────────┐
│          Adapter (Web, Persistence)     │
├─────────────────────────────────────────┤
│          Application (Use Cases)        │
├─────────────────────────────────────────┤
│          Domain (Entities)              │
└─────────────────────────────────────────┘
```

#### Example 1 (`clean/example1/`)
기본적인 Clean Architecture 구현

```
example1/
├── adapter/               # 어댑터 계층 (외부 인터페이스)
│   ├── in/                # 인바운드 어댑터 (Controller)
│   └── out/               # 아웃바운드 어댑터 (Repository)
├── application/           # 애플리케이션 계층
│   ├── port/              # 포트 인터페이스
│   │   ├── in/            # 인바운드 포트 (Use Case)
│   │   └── out/           # 아웃바운드 포트 (Repository Interface)
│   └── service/           # 비즈니스 로직 구현
├── domain/                # 도메인 계층
│   └── entity/            # 엔티티 (순수 비즈니스 객체)
└── usecase/               # 유스케이스 인터페이스
```

**특징**
- 의존성이 항상 내부로 향함
- 도메인이 외부 계층에 대해 알지 못함
- 포트와 어댑터를 통한 간접 의존

#### Example 2 (`clean/example2/`)
더 실무 지향적인 Clean Architecture 구현

```
example2/
├── adapter/
│   └── web/               # 웹 어댑터 (REST API)
├── application/
│   ├── port/
│   │   └── in/            # 인바운드 포트 정의
│   ├── service/           # 서비스 구현
│   └── usecase/           # 유스케이스 구현
└── domain/                # 도메인 모델
```

**개선 사항**
- 더 명확한 유스케이스 정의
- 실무에서 사용 가능한 구조
- 테스트 용이성 향상

#### Clean Architecture 예제 코드
```java
// Domain Entity (순수 비즈니스 로직)
public class Account {
    private Long id;
    private Money balance;

    public void withdraw(Money amount) {
        if (balance.isLessThan(amount)) {
            throw new InsufficientBalanceException();
        }
        this.balance = balance.subtract(amount);
    }
}

// Inbound Port (Use Case)
public interface WithdrawMoneyUseCase {
    void withdraw(Long accountId, Money amount);
}

// Application Service
@Service
public class WithdrawMoneyService implements WithdrawMoneyUseCase {
    private final LoadAccountPort loadAccountPort;
    private final UpdateAccountPort updateAccountPort;

    @Override
    public void withdraw(Long accountId, Money amount) {
        Account account = loadAccountPort.load(accountId);
        account.withdraw(amount);
        updateAccountPort.update(account);
    }
}

// Outbound Port
public interface LoadAccountPort {
    Account load(Long accountId);
}

// Adapter (Infrastructure)
@Repository
public class AccountPersistenceAdapter implements LoadAccountPort, UpdateAccountPort {
    private final AccountJpaRepository repository;

    @Override
    public Account load(Long accountId) {
        return repository.findById(accountId)
            .map(this::mapToDomain)
            .orElseThrow();
    }
}
```

### 2. Hexagonal Architecture (`hexagonal/`)

포트와 어댑터 패턴 (Ports and Adapters Pattern)

#### 핵심 개념
- 애플리케이션 코어의 독립성
- 포트(Port): 인터페이스
- 어댑터(Adapter): 구현체
- 내부 육각형(비즈니스 로직)과 외부 육각형(인프라) 분리

#### 구조
```
         ┌──────────────────┐
         │   Web Adapter    │
         └────────┬─────────┘
                  │
         ┌────────▼─────────┐
         │   Inbound Port   │
         └────────┬─────────┘
                  │
┌─────────────────▼────────────────────┐
│      Application Core (Hexagon)      │
│         Business Logic               │
└─────────────────┬────────────────────┘
                  │
         ┌────────▼──────────┐
         │  Outbound Port    │
         └────────┬──────────┘
                  │
         ┌────────▼──────────┐
         │  DB Adapter       │
         └───────────────────┘
```

#### 장점
- 기술 독립성 (프레임워크, DB 교체 용이)
- 테스트 용이성 (Mock 사용 쉬움)
- 비즈니스 로직의 명확한 분리

### 3. Layered Architecture (`layered/`)

전통적인 3계층 아키텍처

#### 계층 구조
```
┌─────────────────────────────────┐
│   Presentation Layer (UI)       │  - Controller, DTO
├─────────────────────────────────┤
│   Business Layer (Service)      │  - Service, Business Logic
├─────────────────────────────────┤
│   Data Access Layer (DAO)       │  - Repository, Entity
└─────────────────────────────────┘
```

#### 특징
- 각 계층은 바로 아래 계층에만 의존
- 명확한 책임 분리
- 가장 널리 사용되는 패턴

#### 계층별 역할

**Presentation Layer**
- HTTP 요청/응답 처리
- DTO 변환
- 유효성 검증

**Business Layer**
- 비즈니스 로직 구현
- 트랜잭션 관리
- 도메인 규칙 적용

**Data Access Layer**
- 데이터베이스 접근
- 엔티티 매핑
- 쿼리 실행

#### 예제 코드
```java
// Presentation Layer
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}

// Business Layer
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User createUser(UserRequest request) {
        validateUser(request);
        User user = User.create(request);
        return userRepository.save(user);
    }

    private void validateUser(UserRequest request) {
        // 비즈니스 검증 로직
    }
}

// Data Access Layer
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

### 4. Domain-Driven Design (DDD) (`ddd/`)

도메인 중심 설계

#### 핵심 개념

**Entities (엔티티)**
- 고유 식별자를 가진 객체
- 생명주기가 있음
- 가변 객체

**Value Objects (값 객체)**
- 식별자가 없음
- 불변 객체
- 값으로만 비교

**Aggregates (애그리게이트)**
- 관련된 객체들의 클러스터
- 트랜잭션 일관성 경계
- Aggregate Root를 통해서만 접근

**Repositories (리포지토리)**
- Aggregate 저장 및 조회
- 컬렉션처럼 동작

**Domain Services (도메인 서비스)**
- 엔티티나 값 객체에 속하지 않는 로직
- 여러 도메인 객체를 조율

**Domain Events (도메인 이벤트)**
- 도메인에서 발생한 중요한 사건
- 느슨한 결합

#### 예제
```java
// Entity
@Entity
public class Order {
    @Id
    private OrderId id;  // 식별자

    private CustomerId customerId;
    private List<OrderLine> orderLines;
    private OrderStatus status;

    // 비즈니스 로직
    public void place() {
        validateOrder();
        this.status = OrderStatus.PLACED;
        // 도메인 이벤트 발행
        DomainEvents.raise(new OrderPlacedEvent(this.id));
    }
}

// Value Object
@Embeddable
public class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}

// Aggregate
public class OrderAggregate {
    private Order order;  // Aggregate Root
    private List<OrderLine> orderLines;

    // 외부에서는 Aggregate Root를 통해서만 접근
    public void addOrderLine(Product product, int quantity) {
        OrderLine line = new OrderLine(product, quantity);
        order.addLine(line);
    }
}
```

## 프로젝트 구조

```
architecture-lab/
├── src/
│   ├── main/
│   │   ├── java/org/hyeonqz/architecturelab/
│   │   │   ├── clean/              # Clean Architecture
│   │   │   │   ├── example1/       # 기본 예제
│   │   │   │   │   ├── adapter/
│   │   │   │   │   ├── application/
│   │   │   │   │   ├── domain/
│   │   │   │   │   ├── entity/
│   │   │   │   │   └── usecase/
│   │   │   │   └── example2/       # 실무 예제
│   │   │   │       ├── adapter/web/
│   │   │   │       ├── application/
│   │   │   │       │   ├── port/in/
│   │   │   │       │   ├── service/
│   │   │   │       │   └── usecase/
│   │   │   │       └── domain/
│   │   │   ├── hexagonal/          # Hexagonal Architecture
│   │   │   ├── layered/            # Layered Architecture
│   │   │   └── ddd/                # DDD
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── build.gradle
```

## 실행 방법

### 애플리케이션 실행
```bash
./gradlew :architecture-lab:bootRun
```

### 빌드 & 테스트
```bash
./gradlew :architecture-lab:build
./gradlew :architecture-lab:test
```

## 아키텍처 비교

| 특징 | Layered | Hexagonal | Clean |
|------|---------|-----------|-------|
| 학습 곡선 | 낮음 | 중간 | 높음 |
| 복잡도 | 낮음 | 중간 | 높음 |
| 유연성 | 낮음 | 높음 | 매우 높음 |
| 테스트 용이성 | 중간 | 높음 | 매우 높음 |
| 유지보수성 | 중간 | 높음 | 높음 |
| 적합한 규모 | 소규모 | 중규모 | 대규모 |

## 선택 가이드

### Layered Architecture
- 소규모 프로젝트
- 빠른 개발이 필요한 경우
- 팀원의 경험 수준이 다양한 경우

### Hexagonal Architecture
- 외부 시스템 통합이 많은 경우
- 비즈니스 로직이 복잡한 경우
- 기술 스택 변경 가능성이 있는 경우

### Clean Architecture
- 대규모 프로젝트
- 장기 유지보수가 필요한 경우
- 높은 테스트 커버리지가 요구되는 경우

### DDD
- 복잡한 도메인 로직
- 도메인 전문가와 협업
- 마이크로서비스 아키텍처

## 학습 포인트

### 의존성 방향
- Layered: 위에서 아래로
- Hexagonal: 외부에서 내부로
- Clean: 외부에서 내부로 (엄격)

### 테스트 전략
```java
// Hexagonal/Clean Architecture에서의 테스트
@Test
void testWithdrawMoney() {
    // Given
    LoadAccountPort mockPort = mock(LoadAccountPort.class);
    Account account = new Account(1L, Money.of(1000));
    when(mockPort.load(1L)).thenReturn(account);

    WithdrawMoneyUseCase useCase = new WithdrawMoneyService(mockPort, mock(UpdateAccountPort.class));

    // When
    useCase.withdraw(1L, Money.of(500));

    // Then
    assertThat(account.getBalance()).isEqualTo(Money.of(500));
}
```

### 패키지 구조
- **기능별 (Feature)**: 각 기능이 하나의 패키지
- **계층별 (Layer)**: 각 계층이 하나의 패키지
- **모듈별 (Module)**: 비즈니스 영역별 패키지

## 참고 자료

### 책
- "만들면서 배우는 클린 아키텍처" - Tom Hombergs
- "클린 아키텍처" - Robert C. Martin
- "도메인 주도 설계" - Eric Evans
- "도메인 주도 설계 구현" - Vaughn Vernon

### 온라인 자료
- [The Clean Architecture (블로그)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [DDD Reference](https://www.domainlanguage.com/ddd/reference/)

## 설정 파일

### application.yml
```yaml
spring:
  application:
    name: architecture-lab

  datasource:
    url: jdbc:mysql://localhost:3306/architecture_lab
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

## 주요 의존성

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    runtimeOnly 'com.mysql:mysql-connector-j'
    compileOnly 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

## 패턴별 장단점

### Clean Architecture
**장점**
- 프레임워크 독립성
- 테스트 용이성
- 비즈니스 로직의 명확한 분리

**단점**
- 높은 초기 학습 곡선
- 많은 보일러플레이트 코드
- 작은 프로젝트에는 과도함

### Hexagonal Architecture
**장점**
- 기술 스택 교체 용이
- 비즈니스 로직 테스트 쉬움
- 명확한 경계

**단점**
- 추가적인 추상화 레이어
- 초기 설계 시간 필요

### Layered Architecture
**장점**
- 단순하고 이해하기 쉬움
- 널리 사용되는 패턴
- 빠른 개발 가능

**단점**
- 계층 간 강한 결합
- 비즈니스 로직 분산
- 테스트 어려움

코드 구성은 `src/main/java` 안에 패키지 분리를 통해 위 아키텍처 관련 코드를 구성했습니다. 