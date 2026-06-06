package io.github.hyeonqz.archlab.eda_arch.domainevent.order.application;

import io.github.hyeonqz.archlab.eda_arch.domainevent.order.domain.Order;
import io.github.hyeonqz.archlab.eda_arch.domainevent.order.event.DomainEventOrderPlacedEvent;
import io.github.hyeonqz.archlab.eda_arch.domainevent.order.infra.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainEventOrderService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order placeOrder(String customerId, Long amount) {
        Order order = Order.create(customerId, amount);
        orderRepository.save(order);

        log.info("[B안][Order][{}] 주문 저장 완료 id={}", Thread.currentThread().getName(), order.getId());

        eventPublisher.publishEvent(new DomainEventOrderPlacedEvent(order.getId(), order.getCustomerId(), order.getAmount()));

        log.info("[B안][Order] 이벤트 등록 완료 (리스너는 커밋 후 실행됨)");
        return order;
    }
}
