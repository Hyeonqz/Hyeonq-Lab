package org.hyeonqz.architecture.growth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyeonqz.architecture.growth.layered.ApprovalRequiredException;
import org.hyeonqz.architecture.growth.layered.Order;
import org.hyeonqz.architecture.growth.layered.OrderBatchService;
import org.hyeonqz.architecture.growth.layered.OrderService;
import org.junit.jupiter.api.Test;

/**
 * 시나리오 C, 레이어드 — 규칙이 쌓이자 두 진입점이 드리프트한다 (Step 13 §5).
 * 대화형 경로는 새 규칙(고액 승인)을 강제하는데, 복사로 만들어진 배치 경로는 그것을 빠뜨렸다.
 * 결과: 같은 고액 주문이 경로에 따라 다르게 처리된다 — 불변식을 지킬 단일 장소가 없기 때문.
 */
class LayeredDriftTest {

    private static final BigDecimal HIGH = new BigDecimal("60000"); // 임계(5만) 초과

    @Test
    void 대화형_경로는_승인없는_고액_취소를_막는다() {
        Map<String, Order> store = new HashMap<>();
        OrderService service = new OrderService(store);
        service.register("A-1", HIGH);

        assertThrows(ApprovalRequiredException.class, () -> service.cancel("A-1", false));
    }

    @Test
    void 배치_경로는_같은_규칙을_빠뜨려_고액_주문을_승인없이_취소한다() {
        Map<String, Order> store = new HashMap<>();
        OrderService service = new OrderService(store);
        OrderBatchService batch = new OrderBatchService(store); // 같은 저장소, 복사된 검증

        service.register("A-1", HIGH);
        batch.cancelStale(List.of("A-1"));

        // 버그: 배치 경로에는 규칙 2가 없어 고액 주문이 승인 없이 취소됐다.
        // 대화형이라면 막혔을 바로 그 주문이다 — 이것이 "diff가 폭발하는" 지점.
        assertEquals("CANCELLED", store.get("A-1").getStatus());
    }
}
