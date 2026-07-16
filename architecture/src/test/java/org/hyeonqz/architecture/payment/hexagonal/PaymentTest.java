package org.hyeonqz.architecture.payment.hexagonal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hyeonqz.architecture.payment.hexagonal.domain.Money;
import org.hyeonqz.architecture.payment.hexagonal.domain.Payment;
import org.hyeonqz.architecture.payment.hexagonal.domain.PaymentStatus;
import org.junit.jupiter.api.Test;

/**
 * 헥사고날 도메인 — 규칙이 애그리거트에 살아, 검증에 객체 하나면 된다(DB·프레임워크 없이 밀리초).
 */
class PaymentTest {

    @Test
    void 승인하면_APPROVED_다() {
        Payment payment = Payment.approve("P-1", Money.won(10000));
        assertEquals(PaymentStatus.APPROVED, payment.status());
    }

    @Test
    void 정산_수수료는_금액의_3퍼센트다() {
        Payment payment = Payment.approve("P-1", Money.won(10000));

        Money fee = payment.settle();

        assertEquals(0, new java.math.BigDecimal("300.00").compareTo(fee.amount()));
        assertEquals(PaymentStatus.SETTLED, payment.status());
    }

    @Test
    void 승인_상태에서만_취소할_수_있다() {
        Payment payment = Payment.approve("P-1", Money.won(10000));
        payment.settle();

        assertThrows(IllegalStateException.class, payment::cancel);
    }

    @Test
    void 승인_금액은_양수여야_한다() {
        assertThrows(IllegalArgumentException.class, () -> Payment.approve("P-1", Money.won(0)));
    }
}
