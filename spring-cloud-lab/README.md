# Spring Cloud Lab

Spring Cloud를 활용한 마이크로서비스 아키텍처와 분산 시스템을 학습하는 모듈입니다.

## 개요

Spring Cloud Netflix Eureka를 통한 서비스 디스커버리와 HashiCorp Vault를 통한 중앙화된 시크릿 관리를 구현하며, 마이크로서비스 환경에서 필요한 핵심 패턴을 학습합니다.

## 기술 스택

- Java 21
- Spring Boot 3.4.4
- Spring Cloud 2024.0.1
- Spring Cloud Netflix Eureka (Server & Client)
- Spring Cloud Vault
- HashiCorp Vault
- Spring Data JPA
- H2 Database
- Docker & Docker Compose
- Lombok

## 포트 구성

- **애플리케이션**: 8300
- **Eureka Server**: 8761 (기본)
- **HashiCorp Vault**: 8200
- **H2 Console**: `/h2-console`

## 주요 학습 주제

### 1. Service Discovery - Eureka (`eureka/`)

마이크로서비스 환경에서 서비스 인스턴스를 자동으로 등록하고 발견하는 패턴

#### Eureka Server
서비스 레지스트리 역할을 하는 중앙 서버

**설정**
```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false    # 자기 자신을 등록하지 않음
    fetch-registry: false           # 레지스트리 정보를 가져오지 않음
  server:
    enable-self-preservation: false # 개발 환경에서는 비활성화
```

**서버 구성**
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

#### Eureka Client
Eureka Server에 자동으로 등록되는 마이크로서비스

**설정**
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}

spring:
  application:
    name: my-service
```

**클라이언트 구성**
```java
@SpringBootApplication
@EnableEurekaClient
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
```

#### 서비스 디스커버리 사용
```java
@RestController
public class ServiceController {
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/services")
    public List<String> getServices() {
        return discoveryClient.getServices();
    }

    @GetMapping("/instances/{serviceName}")
    public List<ServiceInstance> getInstances(@PathVariable String serviceName) {
        return discoveryClient.getInstances(serviceName);
    }
}
```

#### Load Balancing with RestTemplate
```java
@Configuration
public class RestTemplateConfig {
    @Bean
    @LoadBalanced  // 자동 로드 밸런싱
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Service
public class UserService {
    @Autowired
    private RestTemplate restTemplate;

    public String callOtherService() {
        // 서비스 이름으로 호출 (Eureka가 실제 IP:Port로 변환)
        return restTemplate.getForObject(
            "http://other-service/api/users",
            String.class
        );
    }
}
```

### 2. Secrets Management - HashiCorp Vault (`vault/`)

민감한 정보(비밀번호, API 키)를 안전하게 중앙 관리

#### Vault 설정 (`vault/config/`)

**VaultConfiguration**
```java
@Configuration
public class VaultConfiguration {
    @Value("${spring.cloud.vault.uri}")
    private String vaultUri;

    @Value("${spring.cloud.vault.token}")
    private String vaultToken;

    @Bean
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = VaultEndpoint.from(URI.create(vaultUri));
        VaultTokenAuthentication authentication = new VaultTokenAuthentication(vaultToken);

        return new VaultTemplate(endpoint, authentication);
    }
}
```

**application.yml**
```yaml
spring:
  cloud:
    vault:
      uri: https://localhost:8200
      token: ${VAULT_TOKEN:your-dev-token}
      ssl:
        trust-store: classpath:vault-keystore.jks
        trust-store-password: changeit
      connection-timeout: 5000
      read-timeout: 15000
      kv:
        enabled: true
        backend: secret
        application-name: spring-cloud-lab

  datasource:
    url: jdbc:h2:mem:vault
    driver-class-name: org.h2.Driver
    username: ${vault.database.username}  # Vault에서 주입
    password: ${vault.database.password}  # Vault에서 주입
```

#### Vault 사용 예제

**서비스에서 시크릿 조회**
```java
@Service
public class SecretService {
    @Autowired
    private VaultTemplate vaultTemplate;

    public String getSecret(String path, String key) {
        VaultResponse response = vaultTemplate.read("secret/data/" + path);
        if (response != null && response.getData() != null) {
            return (String) response.getData().get(key);
        }
        return null;
    }

    public void saveSecret(String path, Map<String, Object> secrets) {
        vaultTemplate.write("secret/data/" + path, secrets);
    }
}
```

**컨트롤러에서 활용**
```java
@RestController
@RequestMapping("/api/secrets")
public class SecretController {
    @Autowired
    private SecretService secretService;

    @GetMapping("/{path}/{key}")
    public ResponseEntity<String> getSecret(
            @PathVariable String path,
            @PathVariable String key) {
        String secret = secretService.getSecret(path, key);
        return ResponseEntity.ok(secret);
    }

    @PostMapping("/{path}")
    public ResponseEntity<Void> saveSecret(
            @PathVariable String path,
            @RequestBody Map<String, Object> secrets) {
        secretService.saveSecret(path, secrets);
        return ResponseEntity.ok().build();
    }
}
```

### 3. Docker Compose로 Vault 실행 (`vault-compose/`)

**docker-compose-vault.yml**
```yaml
version: '3.8'

services:
  vault:
    image: vault:latest
    container_name: vault
    ports:
      - "8200:8200"
    environment:
      VAULT_DEV_ROOT_TOKEN_ID: root-token
      VAULT_DEV_LISTEN_ADDRESS: 0.0.0.0:8200
    cap_add:
      - IPC_LOCK
    volumes:
      - vault-data:/vault/data
      - vault-config:/vault/config
    command: server -dev

volumes:
  vault-data:
  vault-config:
```

**실행 방법**
```bash
cd vault-compose
docker-compose -f docker-compose-vault.yml up -d
```

**Vault 초기화**
```bash
# Vault CLI로 접속
docker exec -it vault sh

# Vault 로그인
vault login root-token

# 시크릿 저장
vault kv put secret/spring-cloud-lab/database username=admin password=secret123

# 시크릿 조회
vault kv get secret/spring-cloud-lab/database
```

## 프로젝트 구조

```
spring-cloud-lab/
├── src/
│   ├── main/
│   │   ├── java/org/hyeonqz/springcloudlab/
│   │   │   ├── eureka/             # Eureka 서버/클라이언트
│   │   │   └── vault/              # Vault 통합
│   │   │       ├── config/         # Vault 설정
│   │   │       ├── entity/         # JPA Entity
│   │   │       ├── repository/     # Repository
│   │   │       ├── service/        # 비즈니스 로직
│   │   │       ├── helper/         # 헬퍼 클래스
│   │   │       ├── util/           # 유틸리티
│   │   │       └── controller/     # REST API
│   │   └── resources/
│   │       ├── application.yml
│   │       └── vault-keystore.jks  # SSL 인증서
│   └── test/
├── vault-compose/
│   └── docker-compose-vault.yml
└── build.gradle
```

## 실행 방법

### 1. HashiCorp Vault 시작
```bash
cd vault-compose
docker-compose -f docker-compose-vault.yml up -d

# Vault에 시크릿 저장
docker exec -it vault sh
vault login root-token
vault kv put secret/spring-cloud-lab/database username=admin password=secret123
```

### 2. Eureka Server 실행 (선택)
```bash
# 별도 프로젝트로 Eureka Server를 실행하거나
# 이 프로젝트 내에서 @EnableEurekaServer로 실행
./gradlew :spring-cloud-lab:bootRun
```

### 3. 애플리케이션 실행
```bash
# Vault 통합 애플리케이션 실행
./gradlew :spring-cloud-lab:bootRun

# 또는 환경변수 설정하여 실행
VAULT_TOKEN=root-token ./gradlew :spring-cloud-lab:bootRun
```

### 4. 빌드 & 테스트
```bash
./gradlew :spring-cloud-lab:build
./gradlew :spring-cloud-lab:test
```

## 주요 엔드포인트

### Eureka Dashboard
- `http://localhost:8761` - 등록된 서비스 목록 확인

### 애플리케이션
- `http://localhost:8300` - 메인 애플리케이션
- `http://localhost:8300/h2-console` - H2 데이터베이스 콘솔

### Vault API
- `https://localhost:8200/ui` - Vault UI (dev 모드)
- `https://localhost:8200/v1/secret/data/spring-cloud-lab/database` - 시크릿 조회 API

## 마이크로서비스 패턴

### 1. Service Discovery Pattern
서비스 인스턴스를 동적으로 찾아 호출

**장점**
- 하드코딩된 IP/Port 제거
- 동적 스케일링 지원
- 장애 인스턴스 자동 제외
- 로드 밸런싱 자동화

**구성 요소**
- Service Registry (Eureka Server)
- Service Provider (Eureka Client)
- Service Consumer (RestTemplate with @LoadBalanced)

### 2. Configuration Management Pattern
설정을 중앙에서 관리

**Vault의 장점**
- 암호화된 저장
- 동적 시크릿 생성
- 접근 제어 (ACL)
- 감사 로그
- 시크릿 버전 관리

### 3. Load Balancing Pattern
여러 인스턴스 간 부하 분산

**Client-Side Load Balancing (Ribbon)**
```java
@LoadBalanced
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

**Server-Side Load Balancing**
- Gateway를 통한 라우팅
- Nginx, HAProxy 등

## 설정 파일

### application.yml
```yaml
server:
  port: 8300
  shutdown: graceful

spring:
  application:
    name: spring-cloud-lab

  cloud:
    vault:
      uri: https://localhost:8200
      token: ${VAULT_TOKEN:root-token}
      ssl:
        trust-store: classpath:vault-keystore.jks
        trust-store-password: changeit
      connection-timeout: 5000
      read-timeout: 15000
      kv:
        enabled: true
        backend: secret

  datasource:
    url: jdbc:h2:mem:vault
    driver-class-name: org.h2.Driver

  h2:
    console:
      enabled: true

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
```

## Spring Cloud 핵심 개념

### 1. Service Discovery
- **Client-Side Discovery**: 클라이언트가 직접 레지스트리에서 인스턴스 조회
- **Server-Side Discovery**: 게이트웨이가 레지스트리를 조회하여 라우팅

### 2. Configuration Management
- **Static Configuration**: 파일 기반 (application.yml)
- **Dynamic Configuration**: 런타임 변경 가능 (Vault, Config Server)

### 3. Circuit Breaker (향후 학습)
- Resilience4j
- 서비스 장애 격리
- Fallback 메커니즘

### 4. API Gateway (향후 학습)
- Spring Cloud Gateway
- 라우팅, 필터링
- 인증/인가

## Eureka vs Consul vs Zookeeper

| 특징 | Eureka | Consul | Zookeeper |
|------|--------|--------|-----------|
| 개발사 | Netflix | HashiCorp | Apache |
| 프로토콜 | HTTP | HTTP/DNS | TCP |
| Health Check | Client Heartbeat | TTL/Script | Ephemeral Nodes |
| CAP | AP | CP | CP |
| UI | 제공 | 제공 | 미제공 |

## Vault 시크릿 관리 베스트 프랙티스

### 1. 환경별 시크릿 분리
```bash
# 개발 환경
vault kv put secret/dev/myapp database.password=dev-password

# 운영 환경
vault kv put secret/prod/myapp database.password=prod-password
```

### 2. 동적 시크릿 사용
```bash
# 데이터베이스 동적 자격증명
vault secrets enable database
vault write database/config/mysql \
  plugin_name=mysql-database-plugin \
  connection_url="{{username}}:{{password}}@tcp(localhost:3306)/"
```

### 3. 정책 기반 접근 제어
```hcl
# policy.hcl
path "secret/data/myapp/*" {
  capabilities = ["read"]
}

path "secret/data/admin/*" {
  capabilities = ["create", "read", "update", "delete"]
}
```

### 4. 시크릿 로테이션
- 주기적으로 시크릿 변경
- 자동 갱신 메커니즘 구현
- 이전 버전 유지 (롤백 가능)

## 보안 고려사항

### 1. HTTPS 사용
- Vault는 항상 HTTPS로 통신
- 인증서 검증 필수

### 2. 토큰 관리
- 개발: Root Token (비권장)
- 운영: AppRole, Kubernetes Auth 사용
- 토큰 TTL 설정

### 3. 네트워크 격리
- Vault는 내부 네트워크에만 노출
- 방화벽 규칙 설정

## 학습 포인트

### Eureka
- 서비스 등록/해제 자동화
- Self-Preservation 모드
- 인스턴스 상태 관리 (UP, DOWN, STARTING)
- 레지스트리 캐싱

### Vault
- 시크릿 엔진 (KV, Database, AWS)
- 인증 방법 (Token, AppRole, LDAP)
- 정책 기반 접근 제어
- 감사 로깅

## 참고 자료

### Spring Cloud
- [Spring Cloud 공식 문서](https://spring.io/projects/spring-cloud)
- [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)
- [Spring Cloud Vault](https://spring.io/projects/spring-cloud-vault)

### Eureka
- [Eureka Wiki](https://github.com/Netflix/eureka/wiki)
- [Service Discovery with Eureka](https://spring.io/guides/gs/service-registration-and-discovery/)

### HashiCorp Vault
- [Vault 공식 문서](https://www.vaultproject.io/docs)
- [Vault Getting Started](https://learn.hashicorp.com/vault)
- [Spring Cloud Vault Reference](https://docs.spring.io/spring-cloud-vault/docs/current/reference/html/)

## 트러블슈팅

### Eureka 연결 실패
```bash
# Eureka Server 상태 확인
curl http://localhost:8761/eureka/apps

# 서비스 등록 확인
curl http://localhost:8761/eureka/apps/{SERVICE-NAME}
```

### Vault 연결 실패
```bash
# Vault 상태 확인
docker logs vault

# 시크릿 읽기 권한 확인
vault token capabilities secret/data/spring-cloud-lab/database
```

### SSL 인증서 문제
```bash
# 개발 환경에서는 SSL 검증 비활성화 (비권장)
spring.cloud.vault.ssl.verify=false
```

## 다음 단계

이 프로젝트를 확장하여 학습할 주제:
1. Spring Cloud Gateway - API Gateway 패턴
2. Spring Cloud Config - 중앙화된 설정 관리
3. Resilience4j - Circuit Breaker, Rate Limiter
4. Spring Cloud Sleuth & Zipkin - 분산 추적
5. Spring Cloud Stream - 이벤트 기반 마이크로서비스

## 주요 의존성

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    implementation 'org.springframework.cloud:spring-cloud-starter-vault-config'
    runtimeOnly 'com.h2database:h2'
    compileOnly 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:2024.0.1"
    }
}
```

## 최근 변경사항

- HashiCorp Vault 통합
- Vault HTTPS 통신 설정
- H2 데이터베이스 자동 초기화
- Graceful Shutdown 설정
- Eureka Client 통합
