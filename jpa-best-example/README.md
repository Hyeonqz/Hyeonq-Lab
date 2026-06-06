# JPA Best Example

Spring Boot + JPA/Hibernate 환경에서 실무에서 자주 겪는 성능 문제와 안티패턴을 직접 코드로 검증하고 정리한 레퍼런스 프로젝트입니다.

## Tech Stack

| 항목 | 버전 |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | Boot 관리 |
| Hibernate | Boot 관리 |
| MySQL | 8.x |
| HikariCP | Boot 기본 |
| Lombok | - |


## 패키지 구조

```
src/main/java/io/github/hyeonqz/jpabestexample/
├── defaults/              # 기본 엔티티 및 Audit, Fetch 예제
│   ├── entity/            # Author, Book, Payment, Product, BaseEntity
│   ├── repository/
│   └── service/
├── onetomany/             # @OneToMany 연관관계 예제
├── manytomany/            # @ManyToMany 연관관계 예제
├── onetoone/              # @OneToOne 연관관계 예제
├── associationfk/
│   ├── id/                # FK를 ID로 사용하는 연관관계
│   └── option/            # FK 옵션별 연관관계
└── config/                # DataSource, JPA, Proxy 설정
```

## 학습 내용

### 1. 연관관계 (Association)

연관관계 유형별 효율성 비교와 올바른 구성 방법을 다룹니다.

#### @OneToMany
- **양방향 @OneToMany를 사용하라** — 단방향은 숨겨진 연결 테이블이 생성되어 불필요한 JOIN과 INSERT가 추가된다.
- `cascade = CascadeType.ALL`, `mappedBy`, `orphanRemoval = true`를 부모 측에 지정한다.
- `@ManyToOne`은 기본이 Eager이므로 반드시 `fetch = FetchType.LAZY`를 명시한다.
- `toString()`에 연관관계 필드를 포함하면 LazyInitializationException이 발생할 수 있다.

```java
// 부모(Author)
@OneToMany(cascade = CascadeType.ALL, mappedBy = "author", orphanRemoval = true)
private List<Book> books = new ArrayList<>();

// 자식(Book)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "author_id")
private Author author;
```

#### @ManyToMany
- `List` 대신 **`Set`을 사용하라** — List는 삭제 시 전체 delete 후 re-insert가 발생한다.
- `CascadeType.ALL`, `CascadeType.REMOVE`를 피하고 `PERSIST`, `MERGE`만 사용한다.
- `orphanRemoval`은 `@ManyToMany`에 지정하지 않는다.

```java
@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private Set<ManyToManyBook> books = new HashSet<>();
```

#### @OneToOne
- `@MapsId`를 사용하는 단방향/양방향을 권장한다.
  - 기본키를 공유해 메모리 사용량이 줄어든다.
  - 부모 조회 시 자식에 대한 N+1 쿼리가 발생하지 않는다.

```java
@MapsId
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "onetoone_author_id")
private OneToOneAuthor author;
```

---

### 2. 식별자 (Identity)

- **`GenerationType.AUTO` 사용을 피하라** — MySQL에서는 별도의 시퀀스 테이블을 생성해 성능이 저하된다. `IDENTITY`를 사용한다.
- `equals()`, `hashCode()`는 데이터베이스 식별자 기반으로 오버라이딩한다. `hashCode()`는 상수 값을 반환해야 한다.
- UUID는 `BINARY(16)` 타입으로 저장한다.

---

### 3. Fetch 전략

- **모든 연관관계를 Lazy로 유지하라** — 필요할 때 수동 fetch(join fetch, EntityGraph)를 사용한다.
- 수정 계획이 없는 데이터 조회는 반드시 `@Transactional(readOnly = true)`를 사용한다.
  - 하이드레이티드 상태(Hydrated State)가 메모리에서 제거되어 더티체킹 비용이 없다.
- 읽기 전용 데이터를 가져올 때는 `join fetch + DTO`가 `fetch join + Entity`보다 적합하다.

```java
// fetch join - 엔티티로 가져올 때
@Query("select a from Author a join fetch a.books b where b.price > ?1")
List<Author> fetchAuthorsBooksByPriceJoinFetch(int price);

// DTO 프로젝션 - 읽기 전용
@Transactional(readOnly = true)
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> { ... }
```

---

### 4. 엔티티 설계

- **불변 엔티티**: `@Immutable` + `@Cache(READ_ONLY)` — 연관관계 없이 설계하며 2차 캐시에 저장된다.
- **Boolean 컬럼 매핑**: `AttributeConverter`를 통해 `YES/NO` 문자열로 저장한다.
- **도메인 이벤트**: `@DomainEvents` + `AbstractAggregateRoot`를 통해 save() 시점에 이벤트를 자동 발행한다.
- **비동기 이벤트 핸들러**: 쓰기 작업이 있으면 `@Transactional(propagation = REQUIRES_NEW)`를 명시한다.
- **Audit**: `@MappedSuperclass` + `@EntityListeners(AuditingEntityListener.class)`로 생성/수정 시간을 자동 관리한다.

---

### 5. 커넥션과 트랜잭션

- `@Transactional`은 `public` 메서드에서만 작동한다. private 메서드에 선언해도 무시된다.
- Repository에 `@Transactional(readOnly = true)`를 클래스 레벨에 선언하고, 쓰기 메서드만 `@Transactional`로 오버라이딩하는 패턴을 권장한다.
- 트랜잭션 전파 기본값은 `REQUIRED`이므로 Repository의 `@Transactional`은 서비스 트랜잭션에 참여한다.

```java
@Transactional(readOnly = true)
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author fetchByName(String name);

    @Transactional
    @Modifying
    @Query("delete from author a where a.genre <> ?1")
    int deleteByNotGenre(String genre);
}
```

---

### 6. DataSource & 커넥션 풀

- Spring Boot는 **HikariCP**를 기본 커넥션 풀로 사용한다.
- `spring.datasource.hikari.*` 프로퍼티로 커스터마이징하거나, `DataSourceBuilder`를 통해 빈을 직접 구성한다.
- **Master/Slave 라우팅**: `AbstractRoutingDataSource` + `TransactionSynchronizationManager`로 `@Transactional(readOnly=true)` 여부에 따라 Slave/Master DB를 자동 분기한다.

```java
public class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master";
    }
}
```

---

### 7. 배치 처리 (Batch)

- `spring.jpa.properties.hibernate.jdbc.batch_size`로 배치 사이즈를 설정한다. (권장: 5~10)
- MySQL 최적화 옵션: `rewriteBatchedStatements=true` (여러 INSERT를 하나의 bulk INSERT로 재작성)
- `saveAll()`은 `merge()`를 사용해 불필요한 SELECT가 선행된다. 대량 처리 시 `persist()` 기반 커스텀 배치 구현을 권장한다.
- 동시 배치: `Fork/Join`, `CompletableFuture.allOf()`를 활용한 병렬 배치 처리 방법을 다룬다.

---

### 8. 모니터링

SQL 쿼리 수 카운팅 및 상세 로깅을 위한 두 가지 방법을 다룹니다.

| 라이브러리 | 용도 |
|---|---|
| `datasource-proxy-spring-boot-starter` | 쿼리 수 카운팅, Slf4j 로깅 |
| `p6spy-spring-boot-starter` | 바인딩 파라미터 포함 전체 SQL 로깅 |

느린 쿼리 로깅:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        session.events.log.LOG_QUERIES_SLOWER_THAN_MS: 25
```

---

### 9. 스키마 관리

- 운영 환경에서는 `ddl-auto`를 `validate` 또는 비활성화하고 **Flyway**로 스키마를 관리한다.
- `spring.flyway.schemas`를 통해 데이터베이스를 자동 생성할 수 있다.

---

## 연관관계 효율성 요약

| 연관관계 | 권장 | 비고 |
|---|---|---|
| `@OneToMany` | 양방향 | 단방향 List는 사용 금지 |
| `@ManyToOne` | 단방향 | 매우 효율적 |
| `@ManyToMany` | Set 기반 양방향 | List는 삭제 성능 저하 |
| `@OneToOne` | `@MapsId` 단방향 | N+1 방지, 메모리 절약 |

## 참고 도구

- [FlexyPool](https://vladmihalcea.com/tutorials/flexypool/) — 커넥션 풀 파라미터 튜닝
- [Hypersistence Optimizer](https://vladmihalcea.com/hypersistence-optimizer/) — JPA/Hibernate 사용 패턴 자동 감지
- QueryDSL / jOOQ — 타입 안전 쿼리 빌더

## 참고 자료

- [Vlad Mihalcea - High-Performance Java Persistence](https://vladmihalcea.com/books/high-performance-java-persistence/)
