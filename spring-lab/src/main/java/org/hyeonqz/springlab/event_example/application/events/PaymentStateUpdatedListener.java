package org.hyeonqz.springlab.event_example.application.events;

import org.hyeonqz.springlab.event_example.repository.PaymentRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PaymentStateUpdatedListener {
	private final PaymentRepository paymentRepository;

	@Async // 비동기 적용
	@EventListener(PaymentStateUpdatedEvent.class)
	@Transactional // 비동기 이므로 다른 트랜잭션을 이용한다.
	public void paymentStateUpdated(PaymentStateUpdatedEvent event) {
		paymentRepository.updateState(event.getRequestDto().amount());
	}
}
