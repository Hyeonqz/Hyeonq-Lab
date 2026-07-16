package org.hyeonqz.architecture.payment.hexagonal.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.hyeonqz.architecture.payment.hexagonal.domain.Money;
import org.hyeonqz.architecture.payment.hexagonal.domain.Payment;
import org.hyeonqz.architecture.payment.hexagonal.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * JPA 통합 테스트 — 실제 JPA/Hibernate 로 저장하고 다시 도메인 애그리거트로 복원되는지 검증한다.
 * 운영은 MySQL(application.yml), 이 테스트는 임베디드 H2 라 MySQL 없이 빌드에서 돌아간다.
 *
 * (Spring Boot 4 에서 @DataJpaTest 슬라이스는 starter-test 에서 분리됐다. 그래서 @SpringBootTest +
 * H2 프로퍼티 오버라이드로 같은 효과를 낸다. flush/clear 로 1차 캐시를 비워 실제 DB 왕복을 강제한다.)
 *
 * 이 테스트가 증명하는 것: 도메인 Payment ↔ PaymentJpaEntity 매핑이 왕복해도 상태가 보존된다.
 */
@SpringBootTest(classes = JpaTestConfig.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:paytest;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(PaymentPersistenceAdapter.class)
@Transactional
class PaymentPersistenceAdapterTest {

    @Autowired
    private PaymentPersistenceAdapter adapter;

    @PersistenceContext
    private EntityManager em;

    @Test
    void 승인_결제를_저장하고_조회하면_애그리거트로_복원된다() {
        adapter.save(Payment.approve("P-1", Money.won(10000)));
        em.flush();
        em.clear(); // 1차 캐시 비움 → getById 가 실제 DB 에서 다시 읽는다

        Payment loaded = adapter.getById("P-1");

        assertEquals("P-1", loaded.id());
        assertEquals(PaymentStatus.APPROVED, loaded.status());
        assertEquals(0, BigDecimal.valueOf(10000).compareTo(loaded.amount().amount()));
    }

    @Test
    void 정산_상태와_수수료가_영속되고_복원된다() {
        Payment payment = Payment.approve("P-2", Money.won(10000));
        payment.settle(); // SETTLED + 수수료 300
        adapter.save(payment);
        em.flush();
        em.clear();

        Payment loaded = adapter.getById("P-2");

        assertEquals(PaymentStatus.SETTLED, loaded.status());
        assertEquals(0, new BigDecimal("300.00").compareTo(loaded.fee().amount()));
    }
}
