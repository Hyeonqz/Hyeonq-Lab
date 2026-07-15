package org.hyeonqz.architecture.hexagonal.application;

import org.hyeonqz.architecture.hexagonal.domain.Order;
import org.hyeonqz.architecture.hexagonal.domain.OrderRepository;
import org.hyeonqz.architecture.hexagonal.domain.Settlement;
import org.hyeonqz.architecture.hexagonal.domain.SettlementLedger;

/**
 * 유스케이스 — 클린 아키텍처의 어휘로는 유스케이스 층, 헥사고날의 어휘로는 육각형의 안쪽.
 * 규칙은 도메인에 있고, 여기는 포트들 사이의 조율만 한다. 구체 기술의 이름이 한 줄도 없다.
 */
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final SettlementLedger settlementLedger;

    public OrderCommandService(OrderRepository orderRepository, SettlementLedger settlementLedger) {
        this.orderRepository = orderRepository;
        this.settlementLedger = settlementLedger;
    }

    public void register(Order order) {
        orderRepository.save(order);
    }

    public void cancel(String orderId) {
        Order order = orderRepository.byId(orderId);
        order.cancel();
        orderRepository.save(order);
    }

    public Settlement settle(String orderId) {
        Order order = orderRepository.byId(orderId);
        Settlement settlement = order.settle();
        orderRepository.save(order);
        settlementLedger.record(settlement);
        return settlement;
    }
}
