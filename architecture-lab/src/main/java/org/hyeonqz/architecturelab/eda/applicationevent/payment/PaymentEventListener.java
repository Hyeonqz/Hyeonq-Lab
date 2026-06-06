package org.hyeonqz.architecturelab.eda.applicationevent.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyeonqz.architecturelab.eda.applicationevent.order.event.AppEventOrderPlacedEvent;
import org.hyeonqz.architecturelab.eda.applicationevent.payment.event.AppEventPaymentCompletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final ApplicationEventPublisher eventPublisher;

    /**
     * [동기 이벤트 리스너]
     * - OrderService 와 같은 스레드에서 실행
     * - 예외 발생 시 OrderService 호출부까지 예외가 전파됨
     * - @Transactional 이 걸려있다면 같은 트랜잭션 범위
     */
    @EventListener
    public void handleOrderPlaced(AppEventOrderPlacedEvent event) {
        log.info("[A안][Payment][SYNC][{}] 결제 처리 시작 orderId={}", Thread.currentThread().getName(), event.orderId());

        // 결제 처리 로직 (동기)
        log.info("[A안][Payment][SYNC] 결제 완료, AppEventPaymentCompletedEvent 발행");
        eventPublisher.publishEvent(new AppEventPaymentCompletedEvent(event.orderId(), event.amount(), "COMPLETED"));
    }
}
