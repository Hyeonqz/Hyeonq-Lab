package org.hyeonqz.architecture.hexagonal.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hyeonqz.architecture.hexagonal.adapter.InMemoryOrderRepository;
import org.hyeonqz.architecture.hexagonal.domain.Order;
import org.hyeonqz.architecture.hexagonal.domain.Settlement;
import org.junit.jupiter.api.Test;

/**
 * 이 테스트가 곧 "주도 어댑터"다 — REST 컨트롤러와 동등한 자격으로 유스케이스를 두드린다 (Step 08).
 * 피주도 포트에는 목을 꽂는다: 원장 포트는 메서드 참조(recorded::add) 하나로 대체된다.
 */
class OrderCommandServiceTest {

    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private final List<Settlement> recorded = new ArrayList<>();
    private final OrderCommandService service = new OrderCommandService(repository, recorded::add);

    @Test
    void 정산하면_원장에_기록된다() {
        service.register(new Order("A-1", new BigDecimal("10000")));

        service.settle("A-1");

        assertEquals(1, recorded.size());
        assertEquals(Order.Status.SETTLED, repository.byId("A-1").status());
    }
}
