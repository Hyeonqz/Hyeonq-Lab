# 식별자

### MySQL 에서 Hibernate 5 Auto 생성자 타입을 피해야 하는 이유
```java
@Entity
public class AuthorBad {
    @Id @GenerateValue(strategy = GenerationType.AUTO)
    private Long id;
}
```

위 Auto 는 MySQL 테이블 생성자를 사용하기에 성능이 좋지 않다 <br>

### equals() 및 hashCode() 올바른 오버라이딩
equals(), hashCode() 는 일반 Java 의 POJO 를 사용하지 않기에 주의해야 한다 <br>

엔티티 동등성에 대한 일관성 유지를 위해 오버라이드를 해야한다 <br> 

### Custom Sequence ID 생성 방법
Spring 의 SequenceStyleGenerator 를 상속받아서 재구현을 하면 된다 <br>

### UUID?
MySQL 에서는 UUID 가 AUTO_INCREMENT 대안으로 많이 사용된다 <br>
-> UUID 는 Binary(16) 으로 type 을 지정하자 <br>
즉 데이터베이스에서 UUID 전용 타입이 없다면 Binary(16) 을 권장한다 <br>
