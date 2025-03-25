package org.hyeonqz.springlab.event_example.application.events;

import org.hyeonqz.springlab.event_example.dto.RequestDto;
import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class PaymentStateUpdatedEvent extends ApplicationEvent {
	private final RequestDto requestDto;

	// 발행할 이벤트를 정의한다. -> 이벤트를 발행한다
	public PaymentStateUpdatedEvent (Object source, RequestDto requestDto) {
		super(source);
		this.requestDto = requestDto;
	}

}
