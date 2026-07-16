package org.hyeonqz.architecture.growth.hexagonal;

import java.util.ArrayList;
import java.util.List;

/**
 * 배치 취소 유스케이스 — 나중에 추가됐지만, 이 경로도 결국 order.cancel()을 부른다.
 *
 * 규칙이 도메인 객체에 살기 때문에, 배치를 짠 사람은 규칙을 복사할 필요가 없다(복사할 것이 없다).
 * 규칙 2(고액 승인)가 나중에 Order.cancel()에 더해져도 이 경로는 자동으로 그것을 강제받는다 —
 * 무인 배치라 approved=false이므로, 고액 주문은 취소되지 않고 "검토 필요" 목록으로 떨어진다.
 * 드리프트가 구조적으로 불가능하다.
 */
public class BatchCancelUseCase {

    private final OrderRepository repository;

    public BatchCancelUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    /** @return 규칙에 막혀 취소되지 못하고 검토가 필요한 주문 id 목록 */
    public List<String> cancelStale(List<String> orderIds) {
        List<String> needsReview = new ArrayList<>();
        for (String orderId : orderIds) {
            Order order = repository.byId(orderId);
            try {
                order.cancel(false); // 무인 배치 — 승인 없음
                repository.save(order);
            } catch (ApprovalRequiredException | IllegalStateException e) {
                needsReview.add(orderId); // 규칙이 이 경로에서도 살아 우회를 막았다
            }
        }
        return needsReview;
    }
}
