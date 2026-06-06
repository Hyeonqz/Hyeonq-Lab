# 엔티티의 플루언트 API 스타일
Entity 를 만드는 방법에 대해서 간단하게 알아보자 <br>
- Setter
- Method

#### 하이버네이트 프록시를 통해 자식 측에서 부모 연관관계 채우기
getOne() 메소드를 사용하여 하이버네이트 프록시 객체를 반환시킨다 <br>
하이버네이트는 초기화되지 않은 프록시의 외래키 값을 설정한다 <br>
영속성 컨텍스트에 올라가 있고 같은 트랜잭션 내부라면 findById() 말고 findOne() 이 좋다 <br>

#### 불변 엔티티 작성 방법
> @Immutable 어노테이션이 지정되야한다.

- 어떤 종류의 연관관계도 포함하지 않아야 한다
- hibernate.cache.use_reference_entries 설정이 true 로 지정되야 한다 <br>

불변 엔티티는 2차 캐시에 저장된다 <br>
```java
@Immutable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "엔티티이름")
```

#### 엔티티 복제
거의 할일은 없겠지만 필요하다면 Cloning 라이브러리를 이용하자 <br>   

#### 더티체킹
더티체킹은 현재 영속성 컨텍스트에 로드된 이후 수정된 관리 중, 엔티티를 플러시 시점에 감지하는 하이버네이트 메커니즘이다 <br>
그런 다음 애플리케이션 을 대신해 적절한 SQL Update 문을 실행한다 <br>
하이버네이트는 관리 엔티티의 속성이 하나만 변경된 경우에도 모든 관리 엔티티를 select 한다 <br>

#### Boolean 을 YES/NO 로 매핑
```java
private Boolean bestSelling; // BooleanType, YesNoType 으로 매핑
```

위 boolean 을 char 로 매핑하는 방법은 AttributeConverter 를 사용한다 <br>
```java
@Converter(autoApply = true)
public class BooleanConverter implements AttributeConverter<Boolean, String> {

    // Boolean -> String 으로 변환한다.
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return attribute == null ? "NO" : "YES";
    }

    // String -> Boolean 으로 변환한다.
    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return !"No".equals(dbData);
    }

}
```

하나 특징은 @Enumerated 어노테이션이 달린 타입에는 적용할 수 없다 <br>
@Converter(autoApply = true) 은 해당 변환기가 모든 Boolean 타입에 적용된다는 뜻이다 <br>
특정 속성에만 지정하려면 false 로 지정하고, 변환이 필요한 필드에 직접 매핑을 해야한다
```java
@Convert(converter= BooleanConverter.class)
private Boolean bestSelling;
```


#### 애그리거트 루트로부터 최적의 도메인 이벤트 발행
스프링 레포지토리로 관리되는 엔티티는 애그리거트 루트 라 한다 <br>
스프링 데이터는 @DomainEvents 어노테이션과 함께 제공된다 <br>
위 옵션은 애그리거트 루트의 메소드에서 사용된다 <br>

@DomainEvents 어노테이션을 갖는 메소드는 스프링 데이터에 의해 인식되며 레포지토리를 통해 엔티티가 저장될 때 마다 자동으로 호출된다 <br>
이 외에도 Spring Data 는 @AfterDomainEventPublication 어노테이션을 제공한다 <br>
이벤트 발행 후 이벤트를 정리하고자 자동으로 호출 돼야 하는 메소드를 지정한다 <br>
```java
    @DomainEvents
    public void publishCreate() {
        // 이벤트 선언
    }
    
    @AfterDomainEventPublication
    public void callbackMethod() {
        // 잠재적으로 도메인 이벤트 리스트 정리
    }
```

Spring Data 는 내부적인 발행 메커니즘을 사용하는 편리한 템플릿 기반 클래스를 제공한다 (AbstractAggregateRoot.registerEvent()) <br>
등록된 이벤트는 Spring Data 레포지토리의 save() 가 호출하면 자동으로 발행되고 발행 후 초기화 된다 <br>

#### 동기식 이벤트 실행
이벤트 핸들러에는 @TransactionalEventListener 어노테이션을 사용한다 <br>
그리고 단계 설정을 통해 이벤트를 발행한 트랜잭션 단계에 명시적으로 바인딩 된다 <br>
```java
    BEFORE_COMMIT,
	AFTER_COMMIT,
	AFTER_ROLLBACK,
	AFTER_COMPLETION
```

기본 설정은 AFTER_COMMIT 이다 <br>
AFTER_COMMIT 은 AFTER_COMPLETION 의 세부화 단계 이다 <br>

```java
@TransactionalEventListener
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void handleCheckReviewEvent() {
    // 이벤트 로직
}
```

위 코드는 새로운 트랜잭션을 명시적으로 요구하는 것이다 <br>
@TransactionalEventListener 위에서 데이트베이스 변경이 필요하면 명시적인 새 트랜잭션 Propagation.REQUIRES_NEW 이 필요하다 <br>
하지만 성능 관점에서 비용이 들기 때문에 잘 알고 사용해야 한다 <br>

#### 비동기식 이벤트 실행
스프링부트에서 @EnableAsync 를 통해 비동기 기능을 활성화 한 후에 @Async 를 붙여 사용한다.
```java
@Async
@TransactionalEventListener
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void handleCheckReviewEvent() {
    // 이벤트 로직
}
```

적절한 커넥션 풀과 데이터베이스 서버를 위해서는 짧은 데이터베이스 트랜잭션을 갖는게 좋다 <br>
#### 비동기식 실행 시:
- 비동기식 실행에 적합한 작업을 실행해야 하는 경우 AFTER_COMMIT 과 함께 이벤트 핸들러를 사용한다
- 해당 작업이 C U 작업이 없을 경우 @Transactional 을 적용하지 않는다
- 해당 작업이 C U 작업이 있을 경우 Propagation.REQUIRES_NEW 를 사용하여 필요할 때 까지 데이터베이스 커넥션을 얻는다.
  - 읽기 작업이 있을 경우 -> @Transactional(readOnly=true, Propagation.REQUIRES_NEW) 를 적용한다.
  - 쓰기,수정 작업이 있을 경우 -> @Transactional(Propagation.REQUIRES_NEW) 를 적용한다.:q
- 비동기 작업이 BEFORE_COMMIT 옵션은 적용하지 않는다.