# Connection & Transaction
Hibernate 5.2 부터는 데이터베이스 커넥션 획득을 실제 필요할 때 까지 지연시킬 수 있다 <br>
단일 데이터베이스일 경우 하이버네이트는 트랜잭션이 시작된 직 후 바로 JDBC 트랜잭션의 데이터베이스 커넥션을 획득한다 <br>
ex) @Transactional 지정된 메소드는 메소드 호출 직후 바로 커넥션을 얻는다 <br>

springboot 는 hikariCP 를 사용하여 db 커넥션을 진행한다 <br>
옵션을 통해 auto-commit 을 끌 수 있다 <br>
```yaml
spring:
  datasource:
    hikari:
      auto-commit: false
  jpa:
    properties:
      hibernate:
        connection:
          provider_disable_autocommit: true
```

### 1. @Transactional(readOnly=true) 의 실제 작동방식
@Transactional 을 통해 데이터베이스 트랜잭션 경계를 명시적으로 구분하고 전체 트랜잭션 기간 동안 하나의 데이터베이스 커넥션만 사용해야 한다.  <br>
즉 모든 SQL문이 하나의 격리 커넥션을 사용하고 모두 동일한 영속성 컨텍스트 범위에서 실행되야 한다 <br>

일반적으로 JPA는 읽기 연산에 트랜잭션을 요구하지 않는다 <br>

비트랜잭션 컨텍스트에서 실행되는 메소드는 개발자에 의해 데이터 쓰기 처리로 변경되기 쉬우므로 @Transactional(readOnly=true) 를 통해 팀원에게 쓰기 작업만 하라고 플래그 역할을 한다 <br>

영속성 컨텍스트에서 엔티티를 로드하는 것은 하이드레이티드 상태 를 통해 하이버네이트에 수행된다 <br>
위를 통해 엔티티는 영속성 컨텍스트에서 구체화 된다 <br>

- 읽기-쓰기(@Transactional)
  - 영속성 컨텍스트에서 엔티티와 하이드레이티드 상태 모두 사용할 수 있다.
  - 하이드레이티드 상태는 더티 체킹, 버전 없는 낙관적 잠금, 2차 캐시에 필요하다.
  - 더티 체킹 메커니즘은 플러시 시점에 하이드 레이티드 상태를 활용한다.
    - 더티체킹은 단순히 현재 엔티티 상태를 하이드레이티드 상태와 비교하고 동일하지 않으면 update 문을 트리거 한다.
- 읽기-전용(@Transactional(readOnly=true))
  - 메모리에서 하이드레이티드 상태가 메모리에서 제거되고, 엔티티만 영속성 컨텍스트에 유지된다.

PersistenceContext 를 활용하면 API 를 통해 영속성 컨텍스트의 내용을 확인할 수 있다.<br>

기본적으로 flush() 는 트랜잭션 커밋 전에 발생한다 <br>

### 2. Spring이 @Transactional 을 무시하는 이유
```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
private long persistAuthor(Author author) {
    Author author = authorRepository.save(author);
    return author.count();
}
```

위 코드를 보면 위 메소드를 호출할 때 새로운 트랜잭션 생성을 요구한다 <br>
그리고 위 메소드 내부로직 2개 save(), count() 는 별도 트랜잭션에서 실행된다 <br>

일반적으로 @Transactional 은 public 메소드에서만 작동된다 <br>

즉 @Transactional 은 public 에서만 사용되어야 한다 <br>

### 3. @Transactional 타임아웃 설정 및 롤백
```java
1. @Transactional(timeout = 10)
```
```yaml
spring:
  transaction:
    default-timeout: 10
```

위 2가지 방법으로 설정할 수 있다. <br>

다른 방법으로는 mysql은 `select sleep(15)` 를 통해 트랜잭션을 멈출 수 있다 <br>

### 4. 레포지토리 인터페이스에서 @Transactional 을 사용하는 이유와 방법
@Transactional 이 없고 JPQL 을 사용하여 쿼리를 실행하면 트랜잭션 컨텍스트를 제공하지 않으므로 트랜잭션에서 실행이 되지 않는다 <br>
즉 스프링은 사용자 정의 쿼리 메소드에 대해 기본 트랜잭션 컨텍스트를 제공하지 않는다 <br>
(save(), findById() 제외) <br>

Repository 계층에 @Transactional 을 붙이는 것은 좋은 방법이다 <br>
대부분의 여러 쿼리들이 트리거 되어 실행되기 때문에 아래와 같은 방법을 추천한다 <br>

```java
@Repository
@Transactional(readOnly=true)
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    Author fetchByName(String name);
    
    @Transactional
    @Modifying
    @Query("delete from author a where a.gnere <> ?1")
    int deleteByNeGenre(String genre);
}
```

fetchByName() 은 @Transactional(readOnly=true) 가 적용이 된다 <br>
deleteByNeGenre 은 @Transactional 이 적용이 된다 <br>

그리고 실제 사용하는 부분은 @Transactional 을 걸어야 한다 <br>
```java
@Transactional
public void test() {
    authorRepository.fetchByName("jin");
    authorRepository.deleteByNeGenre("a");
}
```

위 처럼 작성하면 서비스 메소드의 트랜잭션이 시작하고, deleteByNeGenre 메소드에서 지정한 트랜잭션은 서비스 메소드의 트랜잭션에 참여한다 <br>
즉 레포지토리에 @Transactional 을 적용했다고 해서 새로운 트랜잭션을 시작하지 않고, 기존 트랜잭션에 참여한다 <br>
그 이유는 트랜잭션 메커니즘의 default 값이 Propagation.REQUIRED 전파 규칙을 사용하기 때문이다 <br>




















