package org.hyeonqz.architecture.payment.hexagonal.application;

import org.hyeonqz.architecture.payment.hexagonal.domain.Money;

/**
 * 주도(driving) 포트 — 애플리케이션이 바깥에 제공하는 유스케이스 계약.
 * REST 컨트롤러도, 배치도, 테스트도 이 포트를 통해 애플리케이션을 구동한다.
 */
public interface PaymentUseCase {

    void approve(String paymentId, long amount);

    void cancel(String paymentId);

    /** @return 확정된 정산 수수료 */
    Money settle(String paymentId);
}
