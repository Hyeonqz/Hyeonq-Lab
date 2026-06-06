package io.github.hyeonqz.archlab.eda_arch.applicationevent.order;

import io.github.hyeonqz.archlab.eda_arch.applicationevent.order.event.AppEventOrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppEventOrderService {
    private final ApplicationEventPublisher eventPublisher;

    public AppEventOrder placeOrder(String customerId, Long amount) {
        AppEventOrder order = AppEventOrder.create(customerId, amount);
        log.info("[A안][Order][{}] 주문 생성 id={}", Thread.currentThread().getName(), order.getId());

        eventPublisher.publishEvent(new AppEventOrderPlacedEvent(order.getId(), order.getCustomerId(), order.getAmount()));

        log.info("[A안][Order][{}] publishEvent 반환 - 동기 리스너 완료, 비동기 리스너는 별도 스레드 실행 중", Thread.currentThread().getName());
        return order;
    }
}
