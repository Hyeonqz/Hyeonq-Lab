package org.hyeonqz.springlab.event_example.application.service;

import org.hyeonqz.springlab.event_example.application.events.PaymentStateUpdatedEvent;
import org.hyeonqz.springlab.event_example.dto.RequestDto;
import org.hyeonqz.springlab.event_example.entity.Payment;
import org.hyeonqz.springlab.event_example.repository.PaymentRepository;
import org.hyeonqz.springlab.event_example.repository.ProductionItemRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final ProductionItemRepository productionItemRepository;

	private ApplicationEventPublisher eventPublisher;

	@Transactional
	public void createPayment(RequestDto dto) {

		// dto 값을 받아서 payment 를 생성한다.

		// dto 에 값을 토대로 product_item 을 조회하고 item 을 찾는다.

		// item 이 존재한다면 payment 에 상태를 변경한다 -> 상태변경 이벤트 발생
		eventPublisher.publishEvent(new PaymentStateUpdatedEvent(this, dto));
	}
}
