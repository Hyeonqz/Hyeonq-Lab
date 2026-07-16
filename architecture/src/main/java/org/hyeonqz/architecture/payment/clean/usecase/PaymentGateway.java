package org.hyeonqz.architecture.payment.clean.usecase;

import org.hyeonqz.architecture.payment.clean.entity.Payment;

/**
 * 데이터 접근 경계(output boundary) — 클린 어휘로는 게이트웨이.
 * 헥사고날의 피주도 포트와 같은 것: 유스케이스가 소유한 계약, 구현은 바깥 계층에.
 */
public interface PaymentGateway {

    void save(Payment payment);

    Payment getById(String paymentId);
}
