package io.github.hyeonqz.archlab.eda_arch.domainevent.order.application;

import io.github.hyeonqz.archlab.eda_arch.domainevent.order.domain.Order;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<String> placeOrder(
            @RequestParam String customerId,
            @RequestParam Long amount) {
        Order order = orderService.placeOrder(customerId, amount);
        return ResponseEntity.ok("주문 생성 완료: id=" + order.getId());
    }
}
