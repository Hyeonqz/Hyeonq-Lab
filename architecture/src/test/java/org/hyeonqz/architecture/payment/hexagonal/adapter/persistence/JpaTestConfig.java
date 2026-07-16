package org.hyeonqz.architecture.payment.hexagonal.adapter.persistence;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 이 모듈은 실행 가능한 @SpringBootApplication 이 없는 예제 라이브러리다.
 * 그래서 JPA 테스트가 쓸 최소 부트 설정을 테스트 소스에 둔다 — payment.hexagonal 영속 슬라이스만 스캔.
 * (Spring Boot 4 에서 @EntityScan 은 org.springframework.boot.persistence.autoconfigure 로 이동했다.)
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackageClasses = PaymentJpaEntity.class)
@EnableJpaRepositories(basePackageClasses = PaymentJpaRepository.class)
class JpaTestConfig {
}
