package org.hyeonqz.architecture.hexagonal.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * 규칙이 개념과 동거하므로, 규칙 검증에는 도메인 객체 하나면 된다.
 * 저장소도, 프레임워크도, 컨테이너도 없다 — 밀리초에 끝난다.
 */
class OrderTest {

    @Test
    void 정산_완료된_주문은_취소할_수_없다() {
        Order order = new Order("A-1", new BigDecimal("10000"));
        order.settle();

        assertThrows(IllegalStateException.class, order::cancel);
    }

    @Test
    void 정산_수수료는_금액의_3퍼센트다() {
        Order order = new Order("A-1", new BigDecimal("10000"));

        Settlement settlement = order.settle();

        assertEquals(0, new BigDecimal("300").compareTo(settlement.fee()));
    }

    @Test
    void 취소된_주문은_정산할_수_없다() {
        Order order = new Order("A-1", new BigDecimal("10000"));
        order.cancel();

        assertThrows(IllegalStateException.class, order::settle);
    }
}
