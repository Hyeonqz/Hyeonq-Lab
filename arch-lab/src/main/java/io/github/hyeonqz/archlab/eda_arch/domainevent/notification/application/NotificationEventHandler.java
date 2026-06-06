package io.github.hyeonqz.archlab.eda_arch.domainevent.notification.application;

import io.github.hyeonqz.archlab.eda_arch.domainevent.notification.domain.Notification;
import io.github.hyeonqz.archlab.eda_arch.domainevent.notification.infra.NotificationRepository;
import io.github.hyeonqz.archlab.eda_arch.domainevent.payment.event.DomainEventPaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final NotificationRepository notificationRepository;

    /**
     * 결제 트랜잭션 커밋 후, 별도 스레드에서 알림 처리
     * @Transactional: @Async 로 새 스레드에서 실행되므로 독립된 트랜잭션 필요
     */
    @Async("domainEventExecutor")
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompleted(DomainEventPaymentCompletedEvent event) {
        log.info("[B안][NotificationHandler][{}] 결제 커밋 후 알림 처리 orderId={}", Thread.currentThread().getName(), event.getOrderId());

        Notification notification = Notification.create(
                event.getOrderId(),
                String.format("주문 %d 결제가 완료되었습니다.", event.getOrderId()),
                "PAYMENT_COMPLETED"
        );
        notificationRepository.save(notification);

        log.info("[B안][NotificationHandler] 알림 저장 완료 id={}", notification.getId());
    }
}
