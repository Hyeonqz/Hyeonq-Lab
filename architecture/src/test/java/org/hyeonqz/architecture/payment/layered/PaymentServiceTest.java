package org.hyeonqz.architecture.payment.layered;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * 레이어드 — 규칙 검증에 서비스 + 저장소가 함께 뜬다(저장소가 맵이라 지금은 빠름).
 */
class PaymentServiceTest {

    @Test
    void 승인_후_취소하면_취소된다() {
        PaymentService service = new PaymentService();
        service.approve("P-1", new BigDecimal("10000"));

        service.cancel("P-1");
        // 취소 뒤 다시 정산 시도는 막힌다
        assertThrows(IllegalStateException.class, () -> service.settle("P-1"));
    }

    @Test
    void 정산하면_수수료는_금액의_3퍼센트다() {
        PaymentService service = new PaymentService();
        service.approve("P-1", new BigDecimal("10000"));

        BigDecimal fee = service.settle("P-1");

        assertEquals(0, new BigDecimal("300.00").compareTo(fee));
    }

    @Test
    void 정산_완료된_결제는_취소할_수_없다() {
        PaymentService service = new PaymentService();
        service.approve("P-1", new BigDecimal("10000"));
        service.settle("P-1");

        assertThrows(IllegalStateException.class, () -> service.cancel("P-1"));
    }

    @Test
    void 승인_금액은_양수여야_한다() {
        PaymentService service = new PaymentService();
        assertThrows(IllegalArgumentException.class, () -> service.approve("P-1", BigDecimal.ZERO));
    }
}
