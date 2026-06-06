package org.hyeonqz.architecturelab.eda.applicationevent.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eda/application-event/orders")
@RequiredArgsConstructor
public class AppEventOrderController {
    private final AppEventOrderService orderService;

    // curl -X POST "http://localhost:11000/eda/application-event/orders?customerId=user1&amount=10000"
    @PostMapping
    public ResponseEntity<String> placeOrder(
            @RequestParam String customerId,
            @RequestParam Long amount) {
        AppEventOrder order = orderService.placeOrder(customerId, amount);
        return ResponseEntity.ok("주문 생성 완료: id=" + order.getId());
    }
}
