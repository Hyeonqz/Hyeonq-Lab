package org.hyeonqz.springlab.event_best_example.dto;

import java.math.BigDecimal;

public record CreatePaymentWeeklyTotalAmount(
    Long paymentId,
    Long merchantId,
    BigDecimal totalAmount
) {
}
