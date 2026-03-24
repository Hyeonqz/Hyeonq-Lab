# Reactive Lab 시나리오 - R2DBC + MySQL 전환 가이드

---

## JPA vs R2DBC 차이

### 핵심 차이: 블로킹 vs 논블로킹

```
JPA (Blocking)
  요청 → 스레드 점유 → DB 응답 대기 → 스레드 반환
  → 동시 요청 100개 = 스레드 100개 필요

R2DBC (Non-Blocking)
  요청 → 이벤트 등록 → DB 응답 오면 콜백 실행
  → 동시 요청 100개 = 스레드 소수로 처리
```

### 비교표

| 항목 | JPA | R2DBC |
|------|-----|-------|
| I/O 방식 | 블로킹 (JDBC) | 논블로킹 (Reactive) |
| 반환 타입 | `User`, `List<User>` | `Mono<User>`, `Flux<User>` |
| 영속성 컨텍스트 | O (1차 캐시, dirty checking) | X |
| Lazy Loading | O | X (직접 join 또는 별도 쿼리) |
| 연관관계 매핑 | `@OneToMany`, `@ManyToOne` 등 | 직접 처리 |
| 트랜잭션 | `@Transactional` (동기) | `@Transactional` (Reactor 기반) |
| WebFlux 궁합 | 나쁨 (블로킹 → 이벤트루프 점유) | 좋음 (완전 논블로킹) |
| 학습 난이도 | 낮음 | 높음 |

### WebFlux에서 JPA를 쓰면 안 되는 이유

```java
// 이벤트 루프 스레드에서 블로킹 발생 → 전체 서버 성능 저하
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    return Mono.just(userRepository.findById(id).get()); // 위험: 블로킹
}
```

---

## 프로젝트 설정

### build.gradle

```groovy
dependencies {
    // webflux
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    // actuator
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    // r2dbc
    implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
    // mysql r2dbc driver
    runtimeOnly 'io.asyncer:r2dbc-mysql:1.4.0'
}
```

### application.yaml

```yaml
spring:
  application:
    name: spring-reactive-lab

  r2dbc:
    url: r2dbc:mysql://localhost:3306/reactive_db
    username: root
    password: 1234
    pool:
      initial-size: 5
      max-size: 20
      max-idle-time: 30m

  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql

server:
  port: 8080
```

---

## schema.sql

R2DBC는 JPA의 `ddl-auto`가 없기 때문에 테이블을 직접 정의합니다.

`src/main/resources/schema.sql`

```sql
CREATE TABLE IF NOT EXISTS users (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL,
    age   INT
);
```

---

## 코드 구조

### Entity

JPA의 `@Entity` 대신 단순 클래스 + Spring Data의 `@Table` 사용합니다.

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public class User {

    @Id
    private Long id;
    private String name;
    private String email;
    private int age;
}
```

> `@Entity`, `@Column`, `@GeneratedValue` 등 JPA 애노테이션은 사용하지 않습니다.

### Repository

```java
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Flux<User> findByName(String name);

    Mono<User> findByEmail(String email);
}
```

### Service

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + id)));
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    public Mono<Void> delete(Long id) {
        return userRepository.deleteById(id);
    }
}
```

### Controller

```java
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Flux<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<User> findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> save(@RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return userService.delete(id);
    }
}
```

### 트랜잭션

```java
@Transactional // Reactor 기반 트랜잭션 — Mono/Flux 체인 전체에 적용
public Mono<Order> placeOrder(Order order) {
    return stockRepository.decreaseStock(order.getProductId(), order.getQuantity())
            .then(orderRepository.save(order));
}
```

---

## 테스트

```java
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void findAll() {
        userService.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findById_notFound() {
        userService.findById(999L)
                .as(StepVerifier::create)
                .expectError(RuntimeException.class)
                .verify();
    }
}
```

`StepVerifier`는 `Mono`/`Flux`를 동기적으로 검증하는 Reactor Test 유틸입니다.

---

## 주의사항

| 항목 | 내용 |
|------|------|
| Lazy Loading 없음 | 연관 데이터는 별도 쿼리 or `DatabaseClient`로 JOIN 직접 작성 |
| 영속성 컨텍스트 없음 | dirty checking 없음 → 수정 시 명시적으로 `save()` 호출 필요 |
| schema.sql 필수 | DDL 자동 생성 없음, 직접 관리 |
| `@Transactional` 위치 | Service 레이어에만 선언, 체인 전체에 적용됨 |

---

## 전환 체크리스트

- [x] `build.gradle`: `data-jpa`, `h2` 제거 → `data-r2dbc`, `r2dbc-mysql` 추가
- [x] `application.yaml`: `spring.datasource` → `spring.r2dbc` (MySQL)
- [ ] `schema.sql` 작성
- [ ] Entity: `@Entity` 제거, `@Table` + `@Id` (spring.data) 로 교체
- [ ] Repository: `JpaRepository` → `ReactiveCrudRepository`
- [ ] Service: 반환 타입 `Mono<T>` / `Flux<T>` 로 변경
- [ ] 연관관계: `@OneToMany` 등 제거 후 직접 조회로 처리
- [ ] 테스트: `StepVerifier` 사용
