package org.hyeonqz.architecture.growth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.hyeonqz.architecture.growth.hexagonal.ApprovalRequiredException;
import org.hyeonqz.architecture.growth.hexagonal.BatchCancelUseCase;
import org.hyeonqz.architecture.growth.hexagonal.CancelOrderUseCase;
import org.hyeonqz.architecture.growth.hexagonal.InMemoryOrderRepository;
import org.hyeonqz.architecture.growth.hexagonal.Order;
import org.junit.jupiter.api.Test;

/**
 * 시나리오 C, 헥사고날 — 규칙이 도메인 객체 한 곳에 사니 두 경로가 드리프트할 수 없다 (Step 13 §5).
 * 대화형이든 배치든 결국 order.cancel()을 거치므로, 새 규칙은 두 경로에 자동으로 적용된다.
 */
class HexagonalSinglePlaceTest {

    private static final BigDecimal HIGH = new BigDecimal("60000");

    @Test
    void 대화형_경로는_승인없는_고액_취소를_막는다() {
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        repo.save(new Order("A-1", HIGH));
        CancelOrderUseCase useCase = new CancelOrderUseCase(repo);

        assertThrows(ApprovalRequiredException.class, () -> useCase.cancel("A-1", false));
    }

    @Test
    void 배치_경로도_같은_규칙에_막혀_고액_주문을_취소하지_못한다() {
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        repo.save(new Order("A-1", HIGH));
        BatchCancelUseCase batch = new BatchCancelUseCase(repo);

        List<String> needsReview = batch.cancelStale(List.of("A-1"));

        // 레이어드와 달리 배치도 규칙에 막혔다 — 취소되지 않고 검토 목록으로.
        assertEquals(List.of("A-1"), needsReview);
        assertEquals(Order.Status.CREATED, repo.byId("A-1").status());
    }
}
