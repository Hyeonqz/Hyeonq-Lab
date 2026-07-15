package org.hyeonqz.architecture.layered;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.hyeonqz.architecture.layered.service.OrderService;
import org.junit.jupiter.api.Test;

/**
 * "취소 규칙" 하나를 검증하는데 저장소가 통째로 함께 뜬다.
 * 규칙만 떼어낼 방법이 없다 — 규칙이 서비스에, 서비스가 구체 저장소에 붙어 있으므로.
 * 여기서는 저장소가 맵이라 빠르지만, 실제라면 이 자리에 MySQL 컨테이너가 있다.
 */
class OrderServiceTest {

    @Test
    void 정산_완료된_주문은_취소할_수_없다() {
        OrderService service = new OrderService();
        service.register("A-1", new BigDecimal("10000"));
        service.settle("A-1");

        assertThrows(IllegalStateException.class, () -> service.cancel("A-1"));
    }
}
