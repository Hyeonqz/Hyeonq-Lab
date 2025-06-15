package org.hyeonqz.springlab.event_best_example.handler;

import org.hyeonqz.springlab.event_best_example.dto.CreatePaymentDailyTotalAmount;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreatePaymentDailyTotalAmountHandler {


    @Async // 비동기 동작
    @EventListener(classes = CreatePaymentDailyTotalAmount.class) // 트랜잭션과 상관없이 실행이 되야 한다면 실행
    // @TransactionalEventListener(classes = CreatePaymentDailyTotalAmount.class) 트랜잭션이 걸려있는 메소드에서 실행해야 한다면 위 어노테이션 사용
    public void handleCreatePaymentDailyTotalAmountHandler(CreatePaymentDailyTotalAmount event) {
        log.info("Event Start");
        log.info("실제 이벤트 비즈니스에 따른 로직을 실행한다.");
    }
}
