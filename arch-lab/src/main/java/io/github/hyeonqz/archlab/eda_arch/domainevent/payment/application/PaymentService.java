package io.github.hyeonqz.archlab.eda_arch.domainevent.payment.application;

import io.github.hyeonqz.archlab.eda_arch.domainevent.payment.domain.Payment;
import io.github.hyeonqz.archlab.eda_arch.domainevent.payment.event.DomainEventPaymentCompletedEvent;
import io.github.hyeonqz.archlab.eda_arch.domainevent.payment.infra.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void processPayment(Long orderId, Long amount) {
        Payment payment = Payment.create(orderId, amount);
        paymentRepository.save(payment);

        log.info("[B안][Payment][{}] 결제 저장 완료 id={}", Thread.currentThread().getName(), payment.getId());

        // 결제 트랜잭션 내에서 이벤트 발행 → 커밋 후 NotificationEventHandler 실행
        eventPublisher.publishEvent(new DomainEventPaymentCompletedEvent(payment.getId(), payment.getOrderId(), payment.getAmount()));

        log.info("[B안][Payment] 결제 이벤트 등록 완료");
    }
}
