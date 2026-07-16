package org.hyeonqz.architecture.payment.hexagonal.domain;

/**
 * 피주도(driven) 포트 — 도메인이 소유한 계약. 구현(JPA 어댑터, 인메모리 목)은 바깥에 산다.
 * 메서드가 SQL 이 아니라 도메인의 언어를 닮는다.
 */
public interface PaymentRepository {

    void save(Payment payment);

    Payment getById(String paymentId);
}
