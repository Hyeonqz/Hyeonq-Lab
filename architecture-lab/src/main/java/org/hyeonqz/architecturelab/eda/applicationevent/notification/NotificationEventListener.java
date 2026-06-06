package org.hyeonqz.architecturelab.eda.applicationevent.notification;

import lombok.extern.slf4j.Slf4j;
import org.hyeonqz.architecturelab.eda.applicationevent.payment.event.AppEventPaymentCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationEventListener {

    /**
     * [비동기 이벤트 리스너]
     * - 별도 스레드풀(applicationEventExecutor)에서 실행
     * - 발행자(PaymentEventListener)와 완전히 분리된 스레드
     * - 예외가 발생해도 발행자에 영향 없음
     * - 단, 트랜잭션 컨텍스트는 공유되지 않음
     */
    @Async("applicationEventExecutor")
    @EventListener
    public void handlePaymentCompleted(AppEventPaymentCompletedEvent event) {
        log.info("[A안][Notification][ASYNC][{}] 알림 발송 시작 orderId={}", Thread.currentThread().getName(), event.orderId());

        // 알림 발송 로직 (비동기)
        log.info("[A안][Notification][ASYNC] 알림 발송 완료");
    }
}
