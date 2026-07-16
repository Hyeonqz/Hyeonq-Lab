package org.hyeonqz.architecture.payment.hexagonal.adapter.persistence;

import java.math.BigDecimal;

import org.hyeonqz.architecture.payment.hexagonal.domain.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 영속 모델 — 도메인 애그리거트(Payment)와 **별개의 클래스**다.
 * 이 분리가 헥사고날/DDD 의 핵심: 테이블 구조가 도메인 모델을 오염시키지 못한다.
 * 매핑은 어댑터(PaymentPersistenceAdapter)가 담당한다.
 */
@Entity
@Table(name = "payment_hex")
public class PaymentJpaEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    private BigDecimal fee;

    protected PaymentJpaEntity() { // JPA 요구
    }

    public PaymentJpaEntity(String id, PaymentStatus status, BigDecimal amount, BigDecimal fee) {
        this.id = id;
        this.status = status;
        this.amount = amount;
        this.fee = fee;
    }

    public String getId() { return id; }
    public PaymentStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getFee() { return fee; }
}
