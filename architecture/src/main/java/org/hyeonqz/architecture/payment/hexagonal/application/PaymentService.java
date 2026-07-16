package org.hyeonqz.architecture.payment.hexagonal.application;

import org.hyeonqz.architecture.payment.hexagonal.domain.Money;
import org.hyeonqz.architecture.payment.hexagonal.domain.Payment;
import org.hyeonqz.architecture.payment.hexagonal.domain.PaymentRepository;

/**
 * 유스케이스 구현 — 규칙은 도메인에 있고, 여기는 포트들 사이의 조율만 한다.
 * 구체 기술(JPA, MySQL)의 이름이 한 줄도 없다. 피주도 포트(PaymentRepository)에만 의존한다.
 */
public class PaymentService implements PaymentUseCase {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void approve(String paymentId, long amount) {
        paymentRepository.save(Payment.approve(paymentId, Money.won(amount)));
    }

    @Override
    public void cancel(String paymentId) {
        Payment payment = paymentRepository.getById(paymentId);
        payment.cancel();
        paymentRepository.save(payment);
    }

    @Override
    public Money settle(String paymentId) {
        Payment payment = paymentRepository.getById(paymentId);
        Money fee = payment.settle();
        paymentRepository.save(payment);
        return fee;
    }
}
