package org.hyeonqz.architecture.payment.clean.usecase;

import java.math.BigDecimal;

import org.hyeonqz.architecture.payment.clean.entity.Payment;

/**
 * 응답 모델(response model) — 클린의 특징적 요소. 경계를 넘어 나가는 것은 엔티티가 아니라 이 DTO다.
 * 엔티티가 바깥 계층으로 새지 않도록 유스케이스가 경계에서 변환한다.
 */
public record PaymentResponse(String id, String status, BigDecimal amount, BigDecimal fee) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.id(),
                payment.status().name(),
                payment.amount().amount(),
                payment.fee() == null ? null : payment.fee().amount());
    }
}
