package org.hyeonqz.architecture.layered.controller;

import org.hyeonqz.architecture.layered.service.OrderService;

/**
 * 실제라면 @RestController다. 검증도 변환도 없이 통과만 시킨다 — Step 06의 싱크홀.
 */
public class OrderController {

    private final OrderService orderService = new OrderService();

    public String cancel(String orderId) {
        orderService.cancel(orderId);
        return "OK";
    }
}
