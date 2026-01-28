# Design Pattern Lab

GoF(Gang of Four) 디자인 패턴을 학습하고 실무에 적용 가능한 패턴을 연구하는 모듈입니다.

## 개요

23가지 GoF 디자인 패턴을 순수 Java로 구현하며, 각 패턴의 목적, 사용 시기, 장단점을 이해하고 실제 비즈니스 문제에 적용하는 방법을 학습합니다.

## 기술 스택

- Java 21
- JUnit 5 (Jupiter)
- Gradle

## 디자인 패턴 분류

### 생성 패턴 (Creational Patterns)
객체 생성 메커니즘을 다루는 패턴

1. **Builder** - 복잡한 객체 생성 단계별 구성
2. **Factory Method** - 객체 생성 인터페이스 정의
3. **Abstract Factory** - 관련 객체군 생성
4. **Singleton** - 클래스의 인스턴스가 하나만 존재
5. **Prototype** - 기존 객체를 복제하여 새 객체 생성

### 구조 패턴 (Structural Patterns)
클래스와 객체를 조합하여 더 큰 구조를 만드는 패턴

6. **Adapter** - 호환되지 않는 인터페이스 연결
7. **Bridge** - 추상과 구현 분리
8. **Composite** - 트리 구조로 객체 구성
9. **Decorator** - 객체에 동적으로 기능 추가
10. **Facade** - 복잡한 서브시스템에 단순 인터페이스 제공
11. **Flyweight** - 객체 공유로 메모리 절약
12. **Proxy** - 객체 접근 제어

### 행위 패턴 (Behavioral Patterns)
객체 간 책임 분배와 알고리즘을 다루는 패턴

13. **Chain of Responsibility** - 요청을 여러 핸들러로 전달
14. **Command** - 요청을 객체로 캡슐화
15. **Interpreter** - 문법 해석
16. **Iterator** - 컬렉션 순회
17. **Mediator** - 객체 간 통신 중재
18. **Memento** - 객체 상태 저장/복원
19. **Observer** - 객체 상태 변화 통지
20. **State** - 상태에 따라 행위 변경
21. **Strategy** - 알고리즘 캡슐화
22. **Template Method** - 알고리즘 골격 정의
23. **Visitor** - 객체 구조와 연산 분리

## 주요 디자인 패턴 상세

### 1. Builder Pattern (생성)

복잡한 객체를 단계별로 생성

#### 사용 시기
- 객체 생성 파라미터가 많을 때
- 객체 생성 과정이 복잡할 때
- 불변 객체를 생성할 때

#### 예제
```java
public class User {
    private final String name;      // 필수
    private final String email;     // 필수
    private final int age;          // 선택
    private final String phone;     // 선택
    private final String address;   // 선택

    private User(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.age = builder.age;
        this.phone = builder.phone;
        this.address = builder.address;
    }

    public static class Builder {
        private final String name;
        private final String email;
        private int age;
        private String phone;
        private String address;

        public Builder(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}

// 사용
User user = new User.Builder("John", "john@example.com")
    .age(30)
    .phone("010-1234-5678")
    .build();
```

#### 실무 활용
- DTO 생성
- 복잡한 설정 객체
- 불변 객체 생성

### 2. Factory Method Pattern (생성)

객체 생성 인터페이스를 정의하고 서브클래스가 구체적인 클래스 결정

#### 사용 시기
- 생성할 객체 타입을 미리 알 수 없을 때
- 객체 생성 로직이 복잡할 때
- 생성 책임을 서브클래스에 위임하고 싶을 때

#### 예제
```java
// Product
public interface Payment {
    void processPayment(int amount);
}

public class CreditCardPayment implements Payment {
    @Override
    public void processPayment(int amount) {
        System.out.println("신용카드 결제: " + amount);
    }
}

public class KakaoPayPayment implements Payment {
    @Override
    public void processPayment(int amount) {
        System.out.println("카카오페이 결제: " + amount);
    }
}

// Factory
public abstract class PaymentFactory {
    public abstract Payment createPayment();

    public void processOrder(int amount) {
        Payment payment = createPayment();
        payment.processPayment(amount);
    }
}

public class CreditCardPaymentFactory extends PaymentFactory {
    @Override
    public Payment createPayment() {
        return new CreditCardPayment();
    }
}

public class KakaoPayPaymentFactory extends PaymentFactory {
    @Override
    public Payment createPayment() {
        return new KakaoPayPayment();
    }
}

// 사용
PaymentFactory factory = new CreditCardPaymentFactory();
factory.processOrder(10000);
```

#### 실무 활용
- 결제 시스템 (다양한 결제 수단)
- 알림 시스템 (Email, SMS, Push)
- 로깅 시스템 (파일, DB, 콘솔)

### 3. Singleton Pattern (생성)

클래스의 인스턴스가 하나만 존재하도록 보장

#### 사용 시기
- 리소스 공유가 필요할 때 (DB 커넥션, 설정)
- 상태를 전역으로 관리할 때
- 객체 생성 비용이 클 때

#### 예제
```java
// Thread-Safe Singleton (Enum 방식 - 권장)
public enum DatabaseConnection {
    INSTANCE;

    private Connection connection;

    DatabaseConnection() {
        // 초기화 로직
        this.connection = createConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    private Connection createConnection() {
        // 실제 커넥션 생성
        return null;
    }
}

// 사용
Connection conn = DatabaseConnection.INSTANCE.getConnection();

// Double-Checked Locking (권장하지 않음)
public class Singleton {
    private static volatile Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

#### 주의사항
- 테스트 어려움
- 전역 상태로 인한 결합도 증가
- 멀티스레드 환경에서 동기화 필요

### 4. Strategy Pattern (행위)

알고리즘을 캡슐화하고 런타임에 선택

#### 사용 시기
- 유사한 알고리즘이 여러 개 있을 때
- 알고리즘을 런타임에 선택해야 할 때
- 조건문을 줄이고 싶을 때

#### 예제
```java
// Strategy Interface
public interface DiscountStrategy {
    int applyDiscount(int price);
}

// Concrete Strategies
public class NoDiscount implements DiscountStrategy {
    @Override
    public int applyDiscount(int price) {
        return price;
    }
}

public class PercentageDiscount implements DiscountStrategy {
    private final int percentage;

    public PercentageDiscount(int percentage) {
        this.percentage = percentage;
    }

    @Override
    public int applyDiscount(int price) {
        return price - (price * percentage / 100);
    }
}

public class FixedDiscount implements DiscountStrategy {
    private final int discountAmount;

    public FixedDiscount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public int applyDiscount(int price) {
        return Math.max(0, price - discountAmount);
    }
}

// Context
public class ShoppingCart {
    private DiscountStrategy discountStrategy;

    public void setDiscountStrategy(DiscountStrategy strategy) {
        this.discountStrategy = strategy;
    }

    public int calculateTotal(int price) {
        return discountStrategy.applyDiscount(price);
    }
}

// 사용
ShoppingCart cart = new ShoppingCart();
cart.setDiscountStrategy(new PercentageDiscount(10));
int total = cart.calculateTotal(10000);  // 9000
```

#### 실무 활용
- 할인 정책
- 정렬 알고리즘 선택
- 압축 알고리즘 선택
- 검증 로직

### 5. Observer Pattern (행위)

객체 상태 변화를 관찰자들에게 자동 통지

#### 사용 시기
- 객체 간 일대다 의존성이 필요할 때
- 느슨한 결합이 필요할 때
- 이벤트 기반 시스템

#### 예제
```java
// Subject
public class Stock {
    private String symbol;
    private double price;
    private List<StockObserver> observers = new ArrayList<>();

    public void attach(StockObserver observer) {
        observers.add(observer);
    }

    public void detach(StockObserver observer) {
        observers.remove(observer);
    }

    public void setPrice(double price) {
        this.price = price;
        notifyObservers();
    }

    private void notifyObservers() {
        for (StockObserver observer : observers) {
            observer.update(symbol, price);
        }
    }
}

// Observer Interface
public interface StockObserver {
    void update(String symbol, double price);
}

// Concrete Observers
public class EmailNotifier implements StockObserver {
    @Override
    public void update(String symbol, double price) {
        System.out.println("이메일 발송: " + symbol + " 가격 " + price);
    }
}

public class SMSNotifier implements StockObserver {
    @Override
    public void update(String symbol, double price) {
        System.out.println("SMS 발송: " + symbol + " 가격 " + price);
    }
}

// 사용
Stock stock = new Stock();
stock.attach(new EmailNotifier());
stock.attach(new SMSNotifier());
stock.setPrice(150000);  // 모든 Observer에게 통지
```

#### 실무 활용
- 이벤트 핸들링
- MVC 패턴
- Pub/Sub 시스템
- 반응형 프로그래밍

### 6. Decorator Pattern (구조)

객체에 동적으로 기능 추가

#### 사용 시기
- 상속 없이 기능 확장이 필요할 때
- 런타임에 기능을 추가/제거해야 할 때
- 기능 조합이 많을 때

#### 예제
```java
// Component
public interface Coffee {
    String getDescription();
    double cost();
}

// Concrete Component
public class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "커피";
    }

    @Override
    public double cost() {
        return 2000;
    }
}

// Decorator
public abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public String getDescription() {
        return coffee.getDescription();
    }

    @Override
    public double cost() {
        return coffee.cost();
    }
}

// Concrete Decorators
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", 우유";
    }

    @Override
    public double cost() {
        return coffee.cost() + 500;
    }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", 설탕";
    }

    @Override
    public double cost() {
        return coffee.cost() + 200;
    }
}

// 사용
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
System.out.println(coffee.getDescription());  // "커피, 우유, 설탕"
System.out.println(coffee.cost());            // 2700
```

#### 실무 활용
- I/O 스트림 (BufferedReader, InputStreamReader)
- 로깅 기능 추가
- 캐싱 레이어
- 권한 체크

### 7. Adapter Pattern (구조)

호환되지 않는 인터페이스를 변환

#### 예제
```java
// Target Interface
public interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee (기존 시스템)
public class AdvancedMediaPlayer {
    public void playMp4(String fileName) {
        System.out.println("MP4 재생: " + fileName);
    }

    public void playVlc(String fileName) {
        System.out.println("VLC 재생: " + fileName);
    }
}

// Adapter
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer;

    public MediaAdapter(String audioType) {
        this.advancedPlayer = new AdvancedMediaPlayer();
    }

    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equals("mp4")) {
            advancedPlayer.playMp4(fileName);
        } else if (audioType.equals("vlc")) {
            advancedPlayer.playVlc(fileName);
        }
    }
}

// Client
public class AudioPlayer implements MediaPlayer {
    private MediaAdapter adapter;

    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equals("mp3")) {
            System.out.println("MP3 재생: " + fileName);
        } else if (audioType.equals("mp4") || audioType.equals("vlc")) {
            adapter = new MediaAdapter(audioType);
            adapter.play(audioType, fileName);
        }
    }
}
```

## 프로젝트 구조

```
design-pattern-lab/
├── src/
│   ├── main/
│   │   └── java/org/hyeonqz/example1/
│   │       └── Main.java
│   └── test/
│       └── java/
│           ├── creational/        # 생성 패턴 테스트
│           │   ├── BuilderTest.java
│           │   ├── FactoryTest.java
│           │   └── SingletonTest.java
│           ├── structural/        # 구조 패턴 테스트
│           │   ├── AdapterTest.java
│           │   ├── DecoratorTest.java
│           │   └── ProxyTest.java
│           └── behavioral/        # 행위 패턴 테스트
│               ├── ObserverTest.java
│               ├── StrategyTest.java
│               └── CommandTest.java
└── build.gradle
```

## 실행 방법

### 빌드
```bash
./gradlew :design-pattern-lab:build
```

### 테스트 실행
```bash
# 모든 테스트 실행
./gradlew :design-pattern-lab:test

# 특정 패턴 테스트
./gradlew :design-pattern-lab:test --tests "*BuilderTest"
```

## 패턴 선택 가이드

### 객체 생성이 복잡한 경우
- Builder: 파라미터가 많거나 선택적일 때
- Factory Method: 생성할 타입이 런타임에 결정될 때
- Abstract Factory: 관련된 객체군을 생성할 때

### 기능 확장이 필요한 경우
- Decorator: 런타임에 동적으로 기능 추가
- Strategy: 알고리즘을 교체
- Template Method: 알고리즘 골격은 유지하고 세부 단계만 변경

### 인터페이스 호환이 필요한 경우
- Adapter: 기존 인터페이스를 새 인터페이스로 변환
- Facade: 복잡한 시스템을 단순한 인터페이스로 제공
- Proxy: 객체 접근 제어

## 실무 적용 사례

### 1. 결제 시스템
- Factory Method: 다양한 결제 수단
- Strategy: 할인 정책
- Template Method: 결제 프로세스

### 2. 알림 시스템
- Observer: 이벤트 발생 시 다양한 채널로 통지
- Chain of Responsibility: 알림 우선순위 처리
- Decorator: 알림에 포맷팅, 로깅 추가

### 3. 로깅 시스템
- Singleton: 로거 인스턴스
- Decorator: 로그 레벨, 포맷 추가
- Factory: 로그 타입별 생성

## 안티패턴 주의

### Singleton 남용
- 전역 상태로 인한 테스트 어려움
- 의존성 주입(DI) 컨테이너 사용 권장

### 과도한 패턴 사용
- 단순한 문제에 복잡한 패턴 적용 금지
- YAGNI (You Aren't Gonna Need It) 원칙 준수

### 잘못된 패턴 선택
- 문제에 맞는 적절한 패턴 선택
- 패턴 조합 고려

## 학습 순서 추천

### 1단계: 필수 패턴
- Singleton
- Factory Method
- Strategy
- Observer

### 2단계: 자주 사용되는 패턴
- Builder
- Decorator
- Adapter
- Template Method

### 3단계: 고급 패턴
- Abstract Factory
- Composite
- Chain of Responsibility
- Command

## 참고 자료

### 책
- "Design Patterns: Elements of Reusable Object-Oriented Software" - GoF
- "Head First Design Patterns" - Eric Freeman
- "Effective Java" - Joshua Bloch (Java 패턴)
- "리팩토링" - Martin Fowler

### 온라인 자료
- [Refactoring Guru - Design Patterns](https://refactoring.guru/design-patterns)
- [Source Making - Design Patterns](https://sourcemaking.com/design_patterns)
- [Java Design Patterns](https://java-design-patterns.com/)

## 패턴별 코드 예제

모든 패턴의 상세한 구현은 `src/test/java` 디렉토리의 테스트 코드를 참조하세요.

## 테스트

각 패턴은 JUnit 5를 사용한 단위 테스트로 검증됩니다.

```bash
./gradlew :design-pattern-lab:test --info
```

## 주요 학습 포인트

1. **SOLID 원칙**과 디자인 패턴의 관계
2. 패턴 선택 기준과 적용 시기
3. 실무에서 자주 사용되는 패턴 우선 학습
4. 패턴 조합을 통한 복잡한 문제 해결
5. 과도한 패턴 적용 지양 (단순함 유지)
