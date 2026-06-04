# Java Virtual Thread 기초 가이드

> Java 21+ 기준. 순수 Java 환경에서 가상스레드 동작을 단계별로 분석하기 위한 예제 모음.

---

## 실행 환경 요구사항

| 기능 | 최소 버전 |
|---|---|
| Virtual Threads | Java 21 (stable) |
| ScopedValue | Java 21 (preview) → 23 stable |
| StructuredTaskScope | Java 21 (preview) → 24 stable |

```bash
# preview 기능 활성화 시
javac --enable-preview --release 21 Demo.java
java --enable-preview Demo
```

---

## 1. 기본 생성 방법 3가지

```java
// 방법 1: Thread.ofVirtual()
Thread vt1 = Thread.ofVirtual().start(() -> {
    System.out.println("Virtual thread: " + Thread.currentThread());
});
vt1.join();

// 방법 2: Thread.startVirtualThread() - 가장 간단
Thread vt2 = Thread.startVirtualThread(() -> {
    System.out.println("isVirtual: " + Thread.currentThread().isVirtual());
});
vt2.join();

// 방법 3: ExecutorService (실무에서 주로 사용)
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> System.out.println("via executor"));
}
```

**주목할 점**
- `isVirtual()` → 가상스레드 여부 확인
- `try-with-resources` → executor 자동 종료 (Java 19+의 AutoCloseable)

---

## 2. 플랫폼 스레드 vs 가상스레드 성능 비교

```java
public class CompareThreads {
    public static void main(String[] args) throws Exception {
        int count = 10_000;

        // 플랫폼 스레드 - 메모리 부족 또는 느림
        long start1 = System.currentTimeMillis();
        List<Thread> platformThreads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Thread t = Thread.ofPlatform().start(() -> {
                try { Thread.sleep(100); } catch (Exception e) {}
            });
            platformThreads.add(t);
        }
        for (Thread t : platformThreads) t.join();
        System.out.println("Platform: " + (System.currentTimeMillis() - start1) + "ms");

        // 가상 스레드 - 훨씬 빠름
        long start2 = System.currentTimeMillis();
        List<Thread> virtualThreads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Thread t = Thread.ofVirtual().start(() -> {
                try { Thread.sleep(100); } catch (Exception e) {}
            });
            virtualThreads.add(t);
        }
        for (Thread t : virtualThreads) t.join();
        System.out.println("Virtual: " + (System.currentTimeMillis() - start2) + "ms");
    }
}
```

**분석 포인트**
- 플랫폼 스레드: 10,000개 생성 시 스레드당 ~1MB → 약 10GB 메모리 필요
- 가상 스레드: 수 KB 수준, JVM이 내부적으로 ForkJoinPool(carrier thread)에 스케줄링
- `Thread.sleep()` 호출 시 가상스레드는 carrier thread를 즉시 반납

---

## 3. Blocking I/O에서의 동작 (핵심 개념)

```java
public class BlockingIODemo {
    public static void main(String[] args) throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    String carrier = Thread.currentThread().toString();
                    System.out.println("Task " + taskId + " START - " + carrier);

                    // blocking 발생 시점 → carrier thread 반납, 다른 작업 실행
                    try { Thread.sleep(1000); } catch (Exception e) {}

                    System.out.println("Task " + taskId + " END  - " + Thread.currentThread());
                });
            }
        }
        // 주목: START와 END에서 carrier thread(ForkJoinPool-N)가 달라질 수 있음
    }
}
```

**분석 포인트**
- `START`와 `END`에서 출력되는 carrier thread 번호가 다를 수 있음
- blocking 발생 시 JVM이 가상스레드를 unmount → carrier thread가 다른 가상스레드 실행
- blocking 해제 시 JVM이 가상스레드를 다시 mount (다른 carrier thread일 수 있음)

```
예시 출력:
Task 0 START - VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
Task 1 START - VirtualThread[#22]/runnable@ForkJoinPool-1-worker-2
Task 0 END   - VirtualThread[#21]/runnable@ForkJoinPool-1-worker-3  ← carrier 변경!
```

---

## 4. ThreadLocal vs ScopedValue

```java
public class ScopedValueDemo {
    // ThreadLocal은 가상스레드에서도 동작하지만 권장하지 않음
    // → 가상스레드 수가 많을 경우 메모리 누수 위험
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // Java 21+: ScopedValue 권장 (불변, 상속 가능, 가상스레드 친화적)
    static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

    public static void main(String[] args) {
        ScopedValue.where(USER_ID, "user-123").run(() -> {
            System.out.println("Current user: " + USER_ID.get()); // user-123

            // 중첩 scope - 내부에서만 덮어쓰기
            ScopedValue.where(USER_ID, "user-456").run(() -> {
                System.out.println("Inner scope: " + USER_ID.get()); // user-456
            });

            System.out.println("Back to outer: " + USER_ID.get()); // user-123 복원
        });
    }
}
```

**ThreadLocal vs ScopedValue 비교**

| 항목 | ThreadLocal | ScopedValue |
|---|---|---|
| 변경 가능성 | mutable | immutable |
| 스코프 | 스레드 전체 | 명시적 범위 |
| 자식 스레드 상속 | 명시적 복사 필요 | 자동 상속 |
| 가상스레드 친화성 | 낮음 | 높음 |
| 정리 | 수동 `remove()` | 스코프 종료 시 자동 |

---

## 5. Structured Concurrency (Java 21 Preview / 24 stable)

```java
import java.util.concurrent.StructuredTaskScope;

public class StructuredConcurrencyDemo {
    record UserInfo(String name) {}
    record OrderInfo(String order) {}

    static UserInfo fetchUser(int id) throws InterruptedException {
        Thread.sleep(500); // simulate I/O
        return new UserInfo("Alice");
    }

    static OrderInfo fetchOrder(int id) throws InterruptedException {
        Thread.sleep(300);
        return new OrderInfo("Order#42");
    }

    public static void main(String[] args) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // 두 작업을 병렬로 실행
            StructuredTaskScope.Subtask<UserInfo> userTask = scope.fork(() -> fetchUser(1));
            StructuredTaskScope.Subtask<OrderInfo> orderTask = scope.fork(() -> fetchOrder(1));

            scope.join();           // 둘 다 완료 대기
            scope.throwIfFailed();  // 하나라도 실패 시 예외

            System.out.println(userTask.get().name() + " / " + orderTask.get().order());
        }
        // 소요 시간: ~500ms (순차 800ms 대신 병렬로 최장 시간 기준)
    }
}
```

**ShutdownOnFailure vs ShutdownOnSuccess**

| 정책 | 동작 |
|---|---|
| `ShutdownOnFailure` | 하나라도 실패하면 나머지 취소 |
| `ShutdownOnSuccess` | 하나라도 성공하면 나머지 취소 (race 패턴) |

---

## 핵심 개념 요약

```
가상스레드 구조:
  Virtual Thread (수천~수만 개)
       ↕ mount/unmount (blocking 시점)
  Carrier Thread = ForkJoinPool worker (CPU 코어 수만큼)
       ↕
  OS Thread
```

**가상스레드가 효과적인 경우**
- DB 쿼리, HTTP 호출, 파일 I/O 등 blocking I/O가 많은 작업
- 동시 요청 수가 많아 스레드 풀 크기가 병목인 상황

**가상스레드가 효과 없는 경우**
- CPU-bound 작업 (계산, 암호화, 이미지 처리)
- `synchronized` 블록 내부에서 blocking → Pinning 발생 (carrier thread 점유 유지)

**Pinning 방지**
```java
// 나쁨: synchronized 내부 blocking → Pinning
synchronized (lock) {
    Thread.sleep(100); // carrier thread 점유 유지
}

// 좋음: ReentrantLock 사용
lock.lock();
try {
    Thread.sleep(100); // carrier thread 반납 가능
} finally {
    lock.unlock();
}
```

> Pinning 감지: JVM 옵션 `-Djdk.tracePinnedThreads=full` 추가 시 스택 트레이스 출력

---

## 관련 문서

- [virtual-thread-bank-transfer.md](./virtual-thread-bank-transfer.md) — 은행 이체 시나리오 실습 (플랫폼 vs 가상스레드 벤치마크)
