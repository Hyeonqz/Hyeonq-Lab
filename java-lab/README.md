# Java Lab

순수 Java 언어 기능 및 JDK 라이브러리를 실험하고 학습하는 모듈입니다.

## 개요

Java 21의 최신 기능과 핵심 개념들을 실습하며, 특히 Thread Safety, Sealed Classes, 그리고 다양한 Java 언어 기능들을 탐구합니다.

## 기술 스택

- Java 21
- JUnit 5 (Jupiter)
- Gradle

## 주요 학습 주제

### 1. Sealed Classes (`sealed/`)
Java의 Sealed Classes와 Pattern Matching을 활용한 타입 안전성 강화

#### 파일 구조
- `FXOrder.java` - Sealed interface로 정의된 외환 주문 타입
- `MarketOrder.java` - Market Order 구현체
- `LimitOrder.java` - Limit Order 구현체
- `Pet.java` - Sealed class 예제

#### 특징
- 제한된 상속 계층 구조
- 컴파일 타임 타입 체크
- Pattern matching을 통한 안전한 타입 처리
- Exhaustive switch 표현식

### 2. Thread Safety (`thread_safe/`)
멀티스레드 환경에서의 안전한 프로그래밍 기법

#### 파일 구조
- `ImmutablePerson.java` - 불변 객체를 통한 Thread Safety
- `Thread_Local.java` - ThreadLocal을 활용한 스레드별 격리
- `Concurrent.java` - Concurrent 컬렉션 활용
- `Locks.java` - ReentrantLock 등 명시적 락 사용
- `Main.java` - Thread Safety 예제 실행

#### 주요 개념
- 불변 객체 (Immutable Objects)
- ThreadLocal 패턴
- Concurrent Collections (ConcurrentHashMap, etc.)
- 명시적 락 (ReentrantLock, ReadWriteLock)
- 동기화 메커니즘

### 3. Local Variable Type Inference
- `Var.java` - Java 10의 `var` 키워드 활용

### 4. Data Structures (`datastructure/`)
Java 자료구조 구현 및 활용

## 프로젝트 구조

```
java-lab/
├── src/
│   ├── main/
│   │   └── java/org/hyeonqz/java_lab/
│   │       ├── sealed/              # Sealed Classes & Pattern Matching
│   │       │   ├── FXOrder.java
│   │       │   ├── MarketOrder.java
│   │       │   ├── LimitOrder.java
│   │       │   └── Pet.java
│   │       ├── thread_safe/         # Thread Safety 패턴
│   │       │   ├── ImmutablePerson.java
│   │       │   ├── Thread_Local.java
│   │       │   ├── Concurrent.java
│   │       │   ├── Locks.java
│   │       │   └── Main.java
│   │       ├── datastructure/       # 자료구조 구현
│   │       ├── multiple_extends/    # 다중 상속 패턴
│   │       ├── FXOrderClassic.java  # 전통적인 FX Order 구현
│   │       ├── Var.java             # Type Inference
│   │       └── Main.java
│   └── test/
│       └── java/                    # 단위 테스트
└── build.gradle
```

## 실행 방법

### 빌드
```bash
# java-lab 모듈 빌드
./gradlew :java-lab:build

# 테스트 실행
./gradlew :java-lab:test
```

### 예제 실행
```bash
# Main 클래스 실행
./gradlew :java-lab:run

# Thread Safety 예제 실행
# IDE에서 thread_safe/Main.java를 직접 실행
```

## 학습 포인트

### Sealed Classes
```java
public sealed interface FXOrder
    permits MarketOrder, LimitOrder {
    // 제한된 구현체만 허용
}

// Pattern Matching with Switch
String orderType = switch(order) {
    case MarketOrder m -> "Market";
    case LimitOrder l -> "Limit";
    // 컴파일러가 모든 케이스 검증
};
```

### Thread Safety
```java
// 1. 불변 객체
public record ImmutablePerson(String name, int age) {
    // Thread-safe by design
}

// 2. ThreadLocal
ThreadLocal<User> userContext = new ThreadLocal<>();

// 3. Concurrent Collections
ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

// 4. Explicit Locks
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

### Type Inference
```java
// var 키워드 활용
var list = new ArrayList<String>();
var map = Map.of("key", "value");
```

## 테스트

JUnit 5를 사용한 단위 테스트가 포함되어 있습니다.

```bash
./gradlew :java-lab:test
```

## 참고 자료

- [Java Language Specification](https://docs.oracle.com/javase/specs/)
- [Sealed Classes (JEP 409)](https://openjdk.org/jeps/409)
- [Pattern Matching (JEP 441)](https://openjdk.org/jeps/441)
- [Java Concurrency in Practice](https://jcip.net/)
- [Java Memory Model](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html)

## 주요 개념 정리

### Thread Safety 달성 방법
1. Immutable Objects - 불변 객체 사용
2. Synchronization - 동기화 블록/메서드
3. Volatile - 변수 가시성 보장
4. Atomic Classes - 원자적 연산
5. Concurrent Collections - 동시성 지원 컬렉션
6. ThreadLocal - 스레드별 격리

### Sealed Classes의 장점
- 제한된 상속 계층으로 API 안정성 향상
- Pattern matching과 결합하여 타입 안전성 강화
- 컴파일 타임 완전성 검증
- 도메인 모델링 개선
