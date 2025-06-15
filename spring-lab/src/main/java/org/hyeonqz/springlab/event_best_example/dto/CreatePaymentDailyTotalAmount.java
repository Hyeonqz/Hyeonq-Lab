package org.hyeonqz.springlab.event_best_example.dto;

import java.math.BigDecimal;

public record CreatePaymentDailyTotalAmount(
    Long paymentId,
    Long merchantId,
    BigDecimal totalAmount
) {
}
