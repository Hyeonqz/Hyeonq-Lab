package org.hyeonqz.architecturelab.eda.domainevent.order.application;

import lombok.RequiredArgsConstructor;
import org.hyeonqz.architecturelab.eda.domainevent.order.domain.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eda/domain-event/orders")
@RequiredArgsConstructor
public class DomainEventOrderController {
    private final DomainEventOrderService orderService;

    // curl -X POST "http://localhost:11000/eda/domain-event/orders?customerId=user1&amount=10000"
    @PostMapping
    public ResponseEntity<String> placeOrder(
            @RequestParam String customerId,
            @RequestParam Long amount) {
        Order order = orderService.placeOrder(customerId, amount);
        return ResponseEntity.ok("주문 생성 완료: id=" + order.getId());
    }
}
