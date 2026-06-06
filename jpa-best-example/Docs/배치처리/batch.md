~~# Batch
배치 처리는 insert, update, delete 문을 그룹화 할 수 있는 메커니즘으로 결과적으로 데이터베이스 커넥션 및 네트워크 호출 횟수를 크게 줄인다 <br>
일반적으로는 호출 횟수가 적을수록 성능이 향상 된다 <br>

배치 사이즈 설정 -> 일반적으로 application.yml 에서 설정을 한다. 
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 30
```

권장 사이즈는 보통 5~10으로 설정을 한다 <br>

hibernate fetchSize 랑은 다른 개념이므로 착각하면 안됀다 <br>

#### MySQL 배치 최적화
- rewriteBatchedStatements 가 있다.
  - 위 속성이 활성화 되면 SQL문이 하나의 문자열 버퍼로 재작성 되고 하나의 요청으로 전송된다.
  - insert into author(age,name,genre) value (20, "1", "123"), (21, "2", "124"), (22, "3", "133") ---

- cachePrepStmts 의 캐싱을 활성화 한다.

<br>

#### 내장 saveAll(Itreable entities) 단점 확인 및 방지

- 상대적으로 작은 Iterable 을 저장하는데 매우 편리하지만 배치 처리, 특히 많은 양의 엔티티를 처리할 때에는 아래 사이드 이펙트를 알아야 한다.
- 개발자는 현재 트랜잭션에서 영속성 컨텍스트 플러시 및 클리어를 제어할 수 없음.
- 개발자는 merge() 대신 persist() 를 사용할 수 없음
  - merge() 가 호출되면 내부적으로 select 가 실행되고 insert 가 된다.
  - persist() 는 바로 insert 가 진행된다.
- saveAll() 은 영속된 엔티티를 포함하는 List<Iterable s> 를 반환한다.


#### 올바른 방법의 커스텀 구현
배치 처리의 커스텀 구현을 사용하면 프로세스를 제어하고 조정할 수 있다 <br>
여러 최적화를 활용하는 saveInBatch(Iterable entities) 메소드를 사용한다 <br>
- 각 배치 처리 후 데이터베이스 트랜잭션 커밋
- merge() 대신 persist() 사용
- 영속된 엔티티에 List 반환하지 않음

```java
@NoRepositoryBean
public interface BatchRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    
    <S extends T> void saveInBatch(Iterable<S> entities);
}
```

#### 배치 순서 지정
yml 파일에서 order_updates=true 옵션을 통해서 순서를 지정한다 <br>

#### 세션 수준에서 배치 크기 제어 방법
애플리케이션 수준에서 배치 크기를 설정하는 것은 아래와 같다 <br>
```yml
spirng:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: {count}
```

위 설정은 모든 하이버네이트 세션에 동일한 배치 크기가 사용된다 <br>

Hibernate5.2 이후로는 전역에 배치 설정이 아닌 각 데이터베이스 세션 수준에서 배치 크기를 설정할 수 있다 <br>
`Session.setJdbcBatchSize()` 메소드를 사용하여 배치 크기를 조정할 수 있다 <br>
```java
Session session = entityManager.unWrap(Session.class);
session.setJdbcBatchSize(30);
```

#### JDBC 배치 처리 방법
springboot 에서는 jdbcTemplate, jdcbClient 를 통해 배치 처리가 가능하다 <br>

> Fork/Join 배치 처리

배치 처리를 순차적으로 수행하는 대신 동시에 실행하는 것이 좋다 <br>
Java 에는 Executors, Fork/Join 프레임워크, Future 등과 같은 여러 방법이 존재한다 <br>

위 방법들은 큰 작업을 수행할 때 병렬로 수행할 수 있는 더 작은 작업을 재귀적으로 분할한 것이다 <br>
최종적으로는 모든 하위 작업이 완료된 후에 해당 결과가 하나의 결과로 결합된다 <br>

각 배치는 자체 트랜잭션으로 실행되므로 포크/조인 스레드 간의 레이드 컨디션을 피하고자 커넥션 풀이 필요한 만큼 커넥션을 제공할 수 있는지 확인해야 한다<br>
스프링부트는 기본적으로 커넥션 풀을 hikariCP 를 사용한다 <br>
포크/조인 스레드 레이스 컨디션을 방지하기 위해서는 커넥션 풀 설정이 필요하다
```yml
spirng:
  datasource:
    hikari:
      maximumPoolSize= 10
      minimumIdle= 10
```

동시 배치 처리를 사용하면 프로세스 속도를 크게 높일 수 있다는 것은 분명하다 <br>

즉 위 작업은 복잡한 배치 처리 시나리오에 대해서 사용하는 것이 좋다 ex) Spring Batch <br>

> CompletableFuture 을 통한 배치 처리

CompletableFuture 비동기 API 를 제공한다
- CompletableFuture.allOf() 가 중요하다
  - 위 메소드는 여러 작업을 비동기 작업으로 실행하고 완료될 때 까지 기다린다.
- CompletableFuture.runAsync()
  - 위 메소드는 비동기 실행 후 결과를 반환하지 않는다. -> 단일 배치일 경어
- 결과 반환이 필요한 경우 -> supplyAsync() 메소드를 이용하자


### 배치 업데이트에 대한 효율적인 처리 방법
> 부모-자식 관계의 배치 업데이트

```java
spring.jpa.properties.hibernate.order_updates=true
```
업데이트가 전체/영속 전이와 함께 부모-자식 관계에 영향이 미치는 경우 업데이트를 순서화 해야한다 <br>

#### 1. 벌크 업데이트
벌크 작업은 레코드 세트를 수정하는데 유용하다. 하지만 아래와 같은 단점이 있다 <br>
- 벌크 업데이트는 영속성 컨텍스트를 유효하지 않은 상태로 남길 수 있다.
- 벌크 업데이트는 낙관점 락의 이점을 얻지 못한다. @Version 무시
- 벌크 삭제는 전이 삭제, orphanRemoval 장점을 이용할 수 없다.

#### 2. bulk 삭제
ex) author 삭제 시 book 또한 삭제되어야 한다. ex) 1:n 관계(@OneToMany) <br>

#### orphanRemoval=true 사용
위 설정은 CascadeType.REMOVE 와 다르닌까 혼동하면 안된다 <br>

deleteAllInBatch 는 orphanRemoval 을 사용하지 않는다 <br>

전체 레코드를 삭제하는 배치가 필요하다면 deleteAllInBatch() 를 활용하자 <br>
