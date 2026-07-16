package org.hyeonqz.architecture.payment.hexagonal.domain;

/** 결제 생명주기 상태. APPROVED 에서만 CANCELLED/SETTLED 로 전이한다(둘은 종료 상태). */
public enum PaymentStatus {
    APPROVED, CANCELLED, SETTLED
}
