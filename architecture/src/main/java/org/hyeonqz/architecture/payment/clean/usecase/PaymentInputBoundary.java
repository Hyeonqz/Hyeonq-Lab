package org.hyeonqz.architecture.payment.clean.usecase;

/**
 * 입력 경계(input boundary) — 바깥(컨트롤러)이 유스케이스를 부르는 계약.
 * 헥사고날의 주도 포트에 대응한다. 요청은 원시 값/DTO, 응답은 response model 로 나간다.
 */
public interface PaymentInputBoundary {

    PaymentResponse approve(String paymentId, long amount);

    PaymentResponse cancel(String paymentId);

    PaymentResponse settle(String paymentId);
}
