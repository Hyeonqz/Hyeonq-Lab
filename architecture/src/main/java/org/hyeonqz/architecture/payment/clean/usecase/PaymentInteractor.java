package org.hyeonqz.architecture.payment.clean.usecase;

import org.hyeonqz.architecture.payment.clean.entity.Money;
import org.hyeonqz.architecture.payment.clean.entity.Payment;

/**
 * 인터랙터(interactor) — 입력 경계를 구현하는 애플리케이션 업무 규칙.
 * 규칙 판단은 엔티티(Payment)에 위임하고, 게이트웨이로 영속을 처리하며, 경계 밖으로는 응답 모델을 낸다.
 * 의존성 규칙: 이 클래스는 안쪽(엔티티)과 추상(게이트웨이)만 알고, 바깥 구현은 모른다.
 */
public class PaymentInteractor implements PaymentInputBoundary {

    private final PaymentGateway gateway;

    public PaymentInteractor(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public PaymentResponse approve(String paymentId, long amount) {
        Payment payment = Payment.approve(paymentId, Money.won(amount));
        gateway.save(payment);
        return PaymentResponse.from(payment);
    }

    @Override
    public PaymentResponse cancel(String paymentId) {
        Payment payment = gateway.getById(paymentId);
        payment.cancel();
        gateway.save(payment);
        return PaymentResponse.from(payment);
    }

    @Override
    public PaymentResponse settle(String paymentId) {
        Payment payment = gateway.getById(paymentId);
        payment.settle();
        gateway.save(payment);
        return PaymentResponse.from(payment);
    }
}
