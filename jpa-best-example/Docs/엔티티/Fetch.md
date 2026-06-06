# Fetch
다이렉트 페칭 또는 ID 로 가져오기는 식별자를 알고 있고 지연 연관관계가 현재 영속성 컨텍스트에서 필요하지 않을 때에 엔티티를 가져오는 바람직한 방법이다 <br>
다이렉트 페칭이라 함은 FetchType 에 따라 엔티티를 로딩하는 연관관계에 따른 방법이다 <br>

가장 좋은 방법은 모든 연관관계를  Lazy 로 유지하고, 해당 연관관계에서 페치가 필요할시 수동 페치를 적용하는게 적합하다 <br>

내부적으로 findById() 는 EntityManger.find() 메소드를 사용한다 <br>

- EntityManger 를 통한 다이렉트 패치
- Hibernate Session 을 통한 페치

- 다이렉트 페치 및 세션 수준 반복 읽기
- ID로 여러 엔티티 다이렉트 fetch
  - IN 연산자를 사용한 쿼리를 활용한다.

#### 미래 영속성 컨텍스트에서 데이터베이스 변경사항 전파를 위한 읽기 전용 엔티티 사용 이유
```java
    @Transactional(readOnly = true)
    public void getProduct() {
        Payment payment = paymentRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
```

읽기 전용 모드로 엔티티를 로드한다. <br>
readOnly 상태는 자동 플러시도 없고, 더티 체킹도 적용되지 않는다 <br>

#### Bytecode 관련 속성 지연 로딩
```java
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] avatar;
```

image 데이터를 byte 코드로 저장을 한다 <br>
속성 지연 로딩은 CLOB, BLOB, VARBINARY 등 많은 데이터를 저장하는 컬럼 타입 또는 별도 요청시 세부 정보가 로드돼야 하는 경우에 유용하다 <br>
- Bytecode Enhancement 의존성 추가
- enableLazyInitialization 설정을 통해 지연 초기화 활성화

하이버네이트 Bytecode Enhancement 는 빌드 타임에 처리되기 때문에 런타임 오버헤드는 추가되지 않는다 <br>

- 속성 지연 로딩과  N+1

#### 스프링 프로젝션을 통한 DTO 가져오기
데이터베이스에서 가져온 데이터는 메모리에 복사된다 -> 영속성 컨텍스트 1차캐쉬 <br>
위 데이터는 하이버네이트의 EntityEntity 인스턴스로 저장되고 하이버네이트 용어로 하이드레이티드 상태라고 한다 <br>

위 하이드레이티드 상태에서는 더티 체킹 메커니즘이 동작한다 <br>
더티체킹은 플러시 시점에 하이드레이티드 상태와 엔티티를 비교하여 변경 사항을 발견하여 update 를 한다 <br>

즉 폐치 작업 후 로드된 결과는 외부 메모리에 상주하는 것이다 <br>

데이터 수정 계획이 없는 경우 읽기-쓰기 모드로 데이터를 엔티티로 가져오지 않아야 하는 좋은 이유다 <br>
위 시나리오에서 읽기-쓰기 모드는 의미 없이 메모리와 CPU 리소스를 소비하며, 이로 인해 애플리케이션은 심각한 성능 저하가 추가 된다 <br>

즉 읽기 전용 엔티티가 필요하면 읽기 전용 모드를 사용한다 (@Transactional(readOnly=true))<br>
이는 하이드레이티드 상태를 버리도록 지시한다 <br>
플러시 처리 및 더티체킹이 없으며 영속성 컨텍스트에는 엔티티만 남는다 <br>
위를 통해 메모리 및 CPU 가 절약된다 <br>

스프링 프로젝션은 인터페이스 기반 닫힌 프로젝션으로 알려져있다.<br>

내부적으로 스프링은 각 엔티티 객체에 대한 프로젝션 인터페이스의 프록시 인스턴스를 생성하고 프록시에 대한 호출은 자동으로 해당 객체로 전달된다 <br>

```java
@Transactional(readOnly=true)
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    // inner join
    @Query("select a from Author a inner join a.books b " + "where b.price >?1")
    List<Author> fetchAuthorsBooksByPriceInnerJoin(int price);
    
    // fetch join
    @Query("select a from Author a join fetch a.books b where b.price > ?1")
    List<Author> fetchAuthorsBooksByPriceJoinFetch(int price);
    
}
```

join fetch 는 JPA 기능으로 하나의 select 를 통해 부모 객체로 연관관계를 초기화 할 수 있다 <br>
fetch 를 통해 모든 값을 select ~ from 사이에 조인된 테이블에 값을 모두 가져온다 <br>

반면에 inner join 을 통해 로우 데이터를 가져오는 경우는<br>
select ~ from 사이에 부모 테이블을 값만 로드된다 <br>

만일 getBook() 처럼 값을 가져와야 할 경우 새로운 select 쿼리가 1개가 더 나가게 된다 <br>

즉 join fetch 랑 join 은 서로 다른 값을 가져온다 <br>

보통 데이터를 엔티티로 가져와야 할 때 fetch join 을 사용한다 <br>
읽기 전용 데이터를 가져올 때는 fetch join 대신 join + dto 를 사용하는 방법이 좋다 <br>

#### Join 문 작성
일반적으로 JPA 에서 제공하는 join 은 inner join 을 의미한다 <br>
- inner
- outer
  - LEFT OUTER JOIN : 왼쪽 테이블에 있는 데이터 가져오기
  - RIGHT OUTER JOIN : 오른쪽 테이블에 있는 데이터 가져오기
  - FULL OUTER JOIN : 두 테이블 중 하나라도 있는 데이터 가져오기
- cross
  - 모든 데이터에 대한 데이터 가져옴, on 또는 where 절이 없는 cross join 은 카테시안 곱을 제공한다 

MySQL 은 Full join 을 지원하지는 않지만, Union, Union All 을 사용하여 시뮬레이션 할 수 있다 <br>