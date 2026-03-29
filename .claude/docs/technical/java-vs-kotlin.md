# Java vs Kotlin 비교 가이드

> Java 개발자를 위한 Kotlin 전환 가이드. 특히 Null 안전성 중심으로 정리.

---

## 1. Null 안전성 (가장 중요한 차이)

### Java의 문제 - NullPointerException 지옥

```java
// Java - 어디서든 NPE 발생 가능
String name = user.getName();           // user가 null이면 NPE
String upper = name.toUpperCase();      // name이 null이면 NPE
String trimmed = upper.trim();          // 체이닝할수록 추적 어려움

// Optional을 써도 장황함
Optional<String> name = Optional.ofNullable(user)
    .map(User::getName)
    .map(String::toUpperCase);
// 그래도 .get() 호출 시 NoSuchElementException 위험
```

### Kotlin의 해결책 - 타입 시스템에 Null 내장

```kotlin
// Kotlin - 컴파일 타임에 null 가능성 구분
var name: String = "hello"      // null 불가 (non-nullable)
var name: String? = null        // null 가능 (nullable)

// null 가능 타입은 반드시 처리해야 컴파일됨
val upper = name.toUpperCase()  // 컴파일 에러! name이 String?이면 불가
```

### Kotlin Null 처리 연산자

```kotlin
val user: User? = findUser(id)

// 1. 안전 호출 연산자 (?.)
val name: String? = user?.name          // user가 null이면 null 반환 (NPE 없음)
val upper: String? = user?.name?.uppercase()  // 체이닝도 안전

// 2. Elvis 연산자 (?:) - null일 때 기본값
val name: String = user?.name ?: "anonymous"  // null이면 "anonymous"
val length: Int = user?.name?.length ?: 0

// 3. Non-null 단언 (!!) - 확실히 null 아닐 때만 사용
val name: String = user!!.name          // null이면 NPE (사용 자제)

// 4. let - null이 아닐 때만 실행
user?.name?.let { name ->
    println("Hello, $name")
}

// 5. 스마트 캐스트 - if로 null 체크 후 자동 캐스팅
if (user != null) {
    println(user.name)   // 이 블록 안에서는 user가 non-null로 자동 인식
}
```

---

## 2. 데이터 클래스

### Java - 보일러플레이트 지옥

```java
public class User {
    private final Long id;
    private final String name;
    private final String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) { /* 50줄... */ }
    @Override
    public int hashCode() { /* ... */ }
    @Override
    public String toString() { /* ... */ }
    // copy()는 직접 구현해야 함
}
```

### Kotlin - 한 줄

```kotlin
data class User(
    val id: Long,
    val name: String,
    val email: String
)
// equals, hashCode, toString, copy() 자동 생성

// copy()로 불변 객체 수정
val updated = user.copy(name = "새이름")
// 구조 분해
val (id, name, email) = user
```

---

## 3. 함수 / 메서드

```kotlin
// 기본값 파라미터 (Java는 오버로딩 필요)
fun createUser(
    name: String,
    email: String,
    role: String = "USER",   // 기본값
    active: Boolean = true
): User { ... }

// 이름 있는 인자 - 순서 무관
createUser(name = "홍길동", email = "hong@test.com")
createUser(email = "hong@test.com", name = "홍길동", active = false)

// 단일 표현식 함수
fun double(x: Int): Int = x * 2
fun isAdult(age: Int) = age >= 18  // 반환 타입 추론
```

---

## 4. 확장 함수 - Java 유틸 클래스 대체

```kotlin
// Java Utils 패턴
StringUtils.isNotBlank(str)
CollectionUtils.isEmpty(list)

// Kotlin 확장 함수 - 기존 클래스에 메서드 추가
fun String.isNotBlankAndTrimmed(): Boolean = this.isNotBlank() && this.trim() == this
fun List<*>.secondOrNull() = if (size >= 2) this[1] else null

// 사용
"hello".isNotBlankAndTrimmed()
listOf(1, 2, 3).secondOrNull()

// Spring에서 유용한 패턴
fun String?.toNonBlankOrNull(): String? = this?.takeIf { it.isNotBlank() }
```

---

## 5. When 표현식 (switch 대체)

```java
// Java switch
String result;
switch (status) {
    case "ACTIVE": result = "활성"; break;
    case "INACTIVE": result = "비활성"; break;
    default: result = "알 수 없음";
}
```

```kotlin
// Kotlin when - 표현식으로 사용 가능
val result = when (status) {
    "ACTIVE" -> "활성"
    "INACTIVE" -> "비활성"
    else -> "알 수 없음"
}

// 타입 체크와 스마트 캐스트
when (response) {
    is SuccessResponse -> response.data     // SuccessResponse로 자동 캐스팅
    is ErrorResponse -> response.message
    else -> throw IllegalStateException()
}

// 범위 체크
when (age) {
    in 0..12 -> "어린이"
    in 13..19 -> "청소년"
    in 20..64 -> "성인"
    else -> "노인"
}
```

---

## 6. 컬렉션 처리

```kotlin
val users = listOf(
    User(1, "Alice", "alice@test.com"),
    User(2, "Bob", "bob@test.com"),
    User(3, "Charlie", "charlie@test.com")
)

// Java Stream과 유사하지만 더 간결
val activeNames = users
    .filter { it.active }
    .map { it.name }
    .sorted()

// null 안전 필터링
val validEmails = users
    .mapNotNull { it.email }  // null 제거하면서 매핑
    .filter { it.contains("@") }

// groupBy
val byRole: Map<String, List<User>> = users.groupBy { it.role }

// 집계
val totalAge = users.sumOf { it.age }
val avgAge = users.map { it.age }.average()
```

---

## 7. String Template

```kotlin
// Java
String msg = "Hello, " + name + "! You have " + count + " messages.";
String.format("User %s (id=%d) logged in", name, id);

// Kotlin
val msg = "Hello, $name! You have $count messages."
val detail = "User ${user.name} (id=${user.id}) logged in"  // 표현식은 {}로 감싸기
val calc = "2 + 2 = ${2 + 2}"
```

---

## 8. Spring Boot에서 실전 비교

### Repository

```kotlin
// Java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByActiveTrue();
}

// Kotlin
@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?          // Optional 대신 nullable
    fun findByActiveTrue(): List<User>
}
```

### Service

```java
// Java - null 처리 지저분
public UserResponse getUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User not found: " + id));

    String displayName = user.getNickname() != null
        ? user.getNickname()
        : user.getName();

    return new UserResponse(user.getId(), displayName, user.getEmail());
}
```

```kotlin
// Kotlin - 깔끔한 null 처리
fun getUser(id: Long): UserResponse {
    val user = userRepository.findById(id)
        .orElseThrow { NotFoundException("User not found: $id") }

    val displayName = user.nickname ?: user.name  // Elvis 연산자

    return UserResponse(user.id, displayName, user.email)
}
```

### DTO

```kotlin
// Kotlin - data class로 간결하게
data class UserResponse(
    val id: Long,
    val name: String,
    val email: String?,      // nullable 명시
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(user: User) = UserResponse(
            id = user.id,
            name = user.nickname ?: user.name,
            email = user.email,
            createdAt = user.createdAt
        )
    }
}
```

---

## 9. Java → Kotlin 마이그레이션 팁

| 상황 | Java | Kotlin |
|------|------|--------|
| Null 체크 | `if (x != null)` | `x?.let { }` 또는 스마트 캐스트 |
| 기본값 | 오버로딩 | 기본 파라미터 |
| DTO | Lombok `@Data` | `data class` |
| 유틸 메서드 | 정적 클래스 | 확장 함수 |
| 상수 | `static final` | `companion object` 또는 `const val` |
| Optional | `Optional<T>` | `T?` (nullable 타입) |
| Stream | `.stream().filter().collect()` | `.filter().toList()` |

---

## 10. 자주 하는 실수

```kotlin
// !! 남발 금지 - null이 아님을 보장할 수 없으면 사용하지 말 것
val name = user!!.name!!.trim()!!  // 안티패턴

// lateinit - 나중에 초기화할 non-null 프로퍼티
lateinit var service: UserService  // DI 대상 등에 사용
// 초기화 전 접근 시 UninitializedPropertyAccessException

// by lazy - 처음 접근 시 초기화
val heavyObject: HeavyObject by lazy { HeavyObject() }
```

---

## 요약

Kotlin을 쓰면 Java에서 겪는 **NPE 문제의 대부분이 컴파일 타임에 잡힙니다.**
nullable(`?`)과 non-nullable을 타입 시스템이 강제하기 때문에,
런타임에서야 터지는 NPE를 사전에 차단할 수 있습니다.

> `String` = 절대 null 아님 (컴파일러 보장)
> `String?` = null 가능 → 반드시 처리해야 컴파일됨
