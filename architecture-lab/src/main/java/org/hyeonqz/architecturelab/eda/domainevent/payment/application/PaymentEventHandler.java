package org.hyeonqz.architecturelab.eda.domainevent.payment.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyeonqz.architecturelab.eda.domainevent.order.event.DomainEventOrderPlacedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final PaymentService paymentService;

    /**
     * [B안 핵심 패턴]
     * @TransactionalEventListener(AFTER_COMMIT): 주문 트랜잭션이 커밋된 후에만 실행
     * @Async: 별도 스레드에서 실행 (주문 응답을 블로킹하지 않음)
     *
     * 결과: 주문 저장 성공 → 커밋 → 결제 처리 시작 (독립 트랜잭션)
     * 결제 실패해도 주문은 롤백되지 않음 (보상 트랜잭션으로 처리)
     */
    @Async("domainEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(DomainEventOrderPlacedEvent event) {
        log.info("[B안][PaymentHandler][{}] 주문 커밋 후 결제 시작 orderId={}", Thread.currentThread().getName(), event.getOrderId());
        paymentService.processPayment(event.getOrderId(), event.getAmount());
    }
}
