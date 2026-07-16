package org.hyeonqz.architecture.payment.hexagonal.adapter.persistence;

import java.util.NoSuchElementException;

import org.hyeonqz.architecture.payment.hexagonal.domain.Money;
import org.hyeonqz.architecture.payment.hexagonal.domain.Payment;
import org.hyeonqz.architecture.payment.hexagonal.domain.PaymentRepository;

import org.springframework.stereotype.Repository;

/**
 * 피주도 어댑터 — 도메인 포트(PaymentRepository)를 JPA 로 구현한다.
 * 화살표: 어댑터 → 도메인(포트). 도메인은 JPA 를 모르고, 어댑터가 양방향 매핑을 책임진다.
 *
 * 이 매핑(도메인 애그리거트 ↔ JPA 엔티티)이 헥사고날의 실체다 — 테이블을 바꿔도 도메인은 그대로다.
 */
@Repository
public class PaymentPersistenceAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    public PaymentPersistenceAdapter(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Payment payment) {
        jpaRepository.save(toEntity(payment));
    }

    @Override
    public Payment getById(String paymentId) {
        return jpaRepository.findById(paymentId)
                .map(this::toDomain)
                .orElseThrow(() -> new NoSuchElementException("payment not found: " + paymentId));
    }

    private PaymentJpaEntity toEntity(Payment payment) {
        return new PaymentJpaEntity(
                payment.id(),
                payment.status(),
                payment.amount().amount(),
                payment.fee() == null ? null : payment.fee().amount());
    }

    private Payment toDomain(PaymentJpaEntity entity) {
        return Payment.reconstitute(
                entity.getId(),
                new Money(entity.getAmount()),
                entity.getStatus(),
                entity.getFee() == null ? null : new Money(entity.getFee()));
    }
}
