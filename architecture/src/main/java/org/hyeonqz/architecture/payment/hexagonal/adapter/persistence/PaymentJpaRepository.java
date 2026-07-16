package org.hyeonqz.architecture.payment.hexagonal.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA 리포지토리 — 영속 모델(PaymentJpaEntity) 전용. 도메인은 이 존재를 모른다. */
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, String> {
}
