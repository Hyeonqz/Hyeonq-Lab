# Redis Lab

Redis를 활용한 캐싱 및 인메모리 데이터 관리를 학습하는 모듈입니다.

## 개요

Spring Data Redis와 Redisson을 사용하여 캐싱 전략, POS 정산 시스템, RDB 동기화 패턴 등을 구현하며, Redis의 다양한 자료구조와 연산을 학습합니다.

## 기술 스택

- Java 21
- Spring Boot 3.4.3
- Spring Data Redis (Lettuce 클라이언트)
- Redisson
- MySQL 8.0
- Spring Data JPA
- Lombok

## 포트 구성

- **애플리케이션**: 9400
- **Redis**: 6379
- **MySQL**: 3306

## 주요 학습 주제

### 1. Spring Data Redis (`nosql/`)

Lettuce 클라이언트를 사용한 Redis 연산

#### Redis 설정 (`nosql/config/`)
```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(2))
            .shutdownTimeout(Duration.ofMillis(100))
            .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

#### Redis Operations
- **String Operations**: 단순 키-값 저장
- **Hash Operations**: 객체 필드별 저장
- **List Operations**: 순서가 있는 데이터
- **Set Operations**: 중복 없는 집합
- **Sorted Set Operations**: 점수 기반 정렬

### 2. Redisson

고급 Redis 클라이언트로 분산 객체 및 동기화 지원

#### 주요 기능
- 분산 락 (Distributed Lock)
- 분산 컬렉션
- Pub/Sub 메시징
- Rate Limiter
- Bloom Filter

```java
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress("redis://localhost:6379")
            .setConnectionPoolSize(10)
            .setConnectionMinimumIdleSize(5);
        return Redisson.create(config);
    }
}
```

### 3. POS 터미널 정산 시스템 (`rdb/`)

#### 도메인 모델
- **Pos**: POS 터미널 정보
- **Settlement**: 정산 정보
- **SettlementHistory**: 정산 이력

#### 비즈니스 플로우
1. POS 터미널 등록
2. 거래 발생 시 임시 데이터를 Redis에 저장
3. 정산 시점에 Redis 데이터를 집계
4. MySQL에 정산 결과 저장
5. Redis 캐시 업데이트

#### POS Service (`rdb/service/PosService`)
```java
@Service
public class PosService {
    private final PosRepository posRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void registerPos(Pos pos) {
        // MySQL에 저장
        Pos saved = posRepository.save(pos);

        // Redis에 캐싱
        String key = "pos:" + saved.getId();
        redisTemplate.opsForValue().set(key, saved, Duration.ofHours(24));
    }

    public Pos findPosWithCache(Long id) {
        String key = "pos:" + id;

        // 캐시 조회
        Pos cached = (Pos) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        // DB 조회 후 캐싱
        Pos pos = posRepository.findById(id).orElseThrow();
        redisTemplate.opsForValue().set(key, pos, Duration.ofHours(24));
        return pos;
    }
}
```

#### Settlement Service (`rdb/service/SettlementService`)
```java
@Service
public class SettlementService {
    public Settlement processSettlement(Long posId) {
        // Redis에서 임시 거래 데이터 집계
        String transactionKey = "pos:transactions:" + posId;
        List<Transaction> transactions = getTransactionsFromRedis(transactionKey);

        // 정산 처리
        Settlement settlement = calculateSettlement(transactions);

        // MySQL에 저장
        Settlement saved = settlementRepository.save(settlement);

        // Redis 캐시 업데이트 및 임시 데이터 삭제
        updateCacheAndCleanup(posId, saved, transactionKey);

        return saved;
    }
}
```

### 4. RDB 동기화 패턴

#### Cache-Aside Pattern (Lazy Loading)
```java
public Data getData(String key) {
    // 1. 캐시 확인
    Data cached = redisTemplate.opsForValue().get(key);
    if (cached != null) {
        return cached;
    }

    // 2. DB에서 조회
    Data data = repository.findByKey(key);

    // 3. 캐시에 저장
    redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(30));

    return data;
}
```

#### Write-Through Pattern
```java
public void saveData(Data data) {
    // 1. DB에 저장
    Data saved = repository.save(data);

    // 2. 캐시에 저장
    redisTemplate.opsForValue().set("data:" + saved.getId(), saved);
}
```

#### Write-Behind Pattern
```java
public void updateData(Data data) {
    // 1. 캐시에 먼저 저장
    redisTemplate.opsForValue().set("data:" + data.getId(), data);

    // 2. 비동기로 DB에 저장
    asyncExecutor.execute(() -> repository.save(data));
}
```

### 5. Redis 자료구조 활용

#### String
```java
// 단순 값 저장
redisTemplate.opsForValue().set("key", "value");
String value = (String) redisTemplate.opsForValue().get("key");

// 숫자 증가
redisTemplate.opsForValue().increment("counter", 1);

// TTL 설정
redisTemplate.opsForValue().set("session:123", user, Duration.ofMinutes(30));
```

#### Hash
```java
// 객체 필드별 저장
redisTemplate.opsForHash().put("user:1", "name", "John");
redisTemplate.opsForHash().put("user:1", "age", "30");

// 전체 조회
Map<Object, Object> user = redisTemplate.opsForHash().entries("user:1");
```

#### List
```java
// 큐 구현
redisTemplate.opsForList().rightPush("queue", "task1");
Object task = redisTemplate.opsForList().leftPop("queue");

// 최근 N개 조회
List<Object> recent = redisTemplate.opsForList().range("recent:items", 0, 9);
```

#### Set
```java
// 집합 연산
redisTemplate.opsForSet().add("tags:1", "java", "spring", "redis");
Set<Object> tags = redisTemplate.opsForSet().members("tags:1");

// 교집합
Set<Object> common = redisTemplate.opsForSet().intersect("tags:1", "tags:2");
```

#### Sorted Set
```java
// 리더보드
redisTemplate.opsForZSet().add("leaderboard", "player1", 1000);
redisTemplate.opsForZSet().add("leaderboard", "player2", 1500);

// 순위 조회
Set<Object> top10 = redisTemplate.opsForZSet().reverseRange("leaderboard", 0, 9);
```

## 프로젝트 구조

```
redis-lab/
├── src/
│   ├── main/
│   │   ├── java/org/hyeonqz/redislab/
│   │   │   ├── rdb/                    # RDB 관련
│   │   │   │   ├── entity/             # JPA Entity
│   │   │   │   │   ├── Pos.java
│   │   │   │   │   ├── Settlement.java
│   │   │   │   │   └── SettlementHistory.java
│   │   │   │   ├── repository/         # JPA Repository
│   │   │   │   │   ├── PosRepository.java
│   │   │   │   │   └── SettlementRepository.java
│   │   │   │   ├── service/            # 비즈니스 로직
│   │   │   │   │   ├── PosService.java
│   │   │   │   │   └── SettlementService.java
│   │   │   │   └── ui/                 # Controller
│   │   │   │       └── PosTerminalController.java
│   │   │   ├── nosql/                  # Redis 관련
│   │   │   │   ├── config/             # Redis 설정
│   │   │   │   ├── entity/             # Redis Entity
│   │   │   │   ├── repository/         # Redis Repository
│   │   │   │   ├── service/            # Redis Service
│   │   │   │   └── ui/                 # Controller
│   │   │   ├── batch/                  # 배치 처리
│   │   │   └── util/
│   │   │       └── DateTimeUtil.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── http/
│   └── test1-example.http              # HTTP 테스트
└── build.gradle
```

## 실행 방법

### 1. Redis 시작
```bash
# Docker로 Redis 실행
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:latest

# Redis CLI 접속
docker exec -it redis redis-cli
```

### 2. MySQL 시작
```bash
docker run -d \
  --name mysql-redis \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=redis_lab \
  -p 3306:3306 \
  mysql:8.0
```

### 3. 애플리케이션 실행
```bash
./gradlew :redis-lab:bootRun
```

### 4. 빌드 & 테스트
```bash
# 빌드
./gradlew :redis-lab:build

# 테스트
./gradlew :redis-lab:test
```

## Redis 명령어

### 기본 명령어
```bash
# 키 조회
KEYS *
KEYS pos:*

# 값 확인
GET key
HGETALL user:1

# TTL 확인
TTL key

# 키 삭제
DEL key

# 모든 키 삭제 (주의!)
FLUSHALL
```

### 디버깅
```bash
# Redis 정보
INFO

# 연결 확인
PING

# 모니터링 (실시간 명령어 확인)
MONITOR

# 슬로우 로그
SLOWLOG GET 10
```

## 설정 파일

### application.yml
```yaml
server:
  port: 9400

spring:
  application:
    name: redis-lab

  datasource:
    url: jdbc:mysql://localhost:3306/redis_lab
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 10
          max-idle: 10
          min-idle: 2
        shutdown-timeout: 100ms

logging:
  level:
    io.lettuce.core.protocol: DEBUG  # Lettuce 프로토콜 디버깅
```

## 캐싱 전략

### 1. Cache-Aside (Lazy Loading)
- 애플리케이션이 캐시를 직접 관리
- 필요할 때만 캐시에 로드
- 가장 일반적인 패턴

**장점**: 필요한 데이터만 캐싱, 캐시 장애 시 서비스 가능
**단점**: 최초 요청 시 느림 (Cache Miss)

### 2. Write-Through
- 쓰기 시 DB와 캐시에 동시 저장
- 데이터 일관성 보장

**장점**: 항상 최신 데이터 보장
**단점**: 쓰기 성능 저하, 사용되지 않는 데이터도 캐싱

### 3. Write-Behind (Write-Back)
- 캐시에 먼저 쓰고 비동기로 DB 저장
- 높은 쓰기 성능

**장점**: 빠른 쓰기, 부하 분산
**단점**: 데이터 손실 위험, 복잡한 구현

### 4. Refresh-Ahead
- TTL 만료 전 미리 갱신
- 항상 따뜻한 캐시 유지

**장점**: Cache Miss 최소화
**단점**: 추가 리소스 필요, 예측 가능한 패턴에만 유용

## 성능 최적화

### Redis 연결 풀 설정
```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 10    # 최대 커넥션 수
          max-idle: 10      # 최대 유휴 커넥션
          min-idle: 2       # 최소 유휴 커넥션
          max-wait: 3000ms  # 커넥션 대기 시간
```

### 직렬화 최적화
```java
// JSON 직렬화 (가독성)
template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

// 성능이 중요한 경우
template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
```

### TTL 전략
- 자주 변경되는 데이터: 짧은 TTL (1-5분)
- 안정적인 데이터: 긴 TTL (1-24시간)
- 정적 데이터: TTL 없음 또는 매우 긴 TTL

## HTTP 테스트

`http/test1-example.http` 파일을 사용하여 API 테스트

```http
### POS 등록
POST http://localhost:9400/api/pos
Content-Type: application/json

{
  "terminalId": "POS-001",
  "storeName": "매장A"
}

### POS 조회 (캐시 활용)
GET http://localhost:9400/api/pos/1

### 정산 처리
POST http://localhost:9400/api/settlement
Content-Type: application/json

{
  "posId": 1,
  "amount": 150000
}
```

## 학습 포인트

### Redis의 장점
- 인메모리 데이터베이스로 빠른 읽기/쓰기
- 다양한 자료구조 지원
- 영속성 옵션 (RDB, AOF)
- Pub/Sub 메시징
- 분산 락 지원

### Redis vs Memcached
- Redis: 다양한 자료구조, 영속성, Pub/Sub
- Memcached: 단순 키-값, 빠른 속도, 낮은 메모리

### Lettuce vs Jedis
- Lettuce: 비동기/논블로킹, Netty 기반, Spring 기본값
- Jedis: 동기/블로킹, 간단한 API

## 모니터링

### Redis 메트릭
```bash
# 메모리 사용량
INFO memory

# 커넥션 수
INFO clients

# 명령어 통계
INFO stats

# 키스페이스 정보
INFO keyspace
```

### Spring Boot Actuator
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

## 참고 자료

- [Redis 공식 문서](https://redis.io/documentation)
- [Spring Data Redis 문서](https://spring.io/projects/spring-data-redis)
- [Redisson 문서](https://github.com/redisson/redisson)
- [Redis Gate - 한글 가이드](http://redisgate.kr/redis/clients/spring_strings.php)
- [Redis in Action](https://redislabs.com/ebook/redis-in-action/)

## 트러블슈팅

### Redis 연결 실패
```bash
# Redis 상태 확인
docker ps | grep redis

# Redis 로그 확인
docker logs redis

# 연결 테스트
redis-cli PING
```

### 캐시 일관성 문제
- Write-Through 패턴 사용
- TTL을 짧게 설정
- 캐시 무효화 전략 구현

### 메모리 부족
```bash
# 메모리 정책 설정
CONFIG SET maxmemory 256mb
CONFIG SET maxmemory-policy allkeys-lru
```

## 최근 변경사항

- POS 터미널 정산 시스템 구현
- Redis 캐시 레이어 추가
- Lettuce 프로토콜 디버깅 활성화
- RDB 동기화 패턴 구현
- Redisson 클라이언트 통합
