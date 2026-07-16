package org.hyeonqz.architecture.growth.layered;

import java.util.List;
import java.util.Map;

/**
 * 배치 취소 경로 — 나중에, 다른 스프린트(어쩌면 다른 사람)에 추가됐다.
 *
 * 취소 검증이 도메인 객체가 아니라 OrderService에 살았으므로, 이 배치를 짠 사람은
 * 그 규칙을 여기로 **복사해왔다.** 그런데 복사한 시점에는 "정산 완료 금지" 규칙만 있었고,
 * 이후 OrderService에 추가된 "고액 승인 필요" 규칙(규칙 2)은 이 경로에 반영되지 않았다.
 *
 * 이것이 "불변식을 지킬 단일 장소가 없다"는 병의 임상 결과다 — 규칙이 드리프트한다.
 */
public class OrderBatchService {

    private final Map<String, Order> store;

    public OrderBatchService(Map<String, Order> store) {
        this.store = store;
    }

    public void cancelStale(List<String> orderIds) {
        for (String orderId : orderIds) {
            Order order = store.get(orderId);
            if ("SETTLED".equals(order.getStatus())) { // 복사해온 규칙 1
                continue;
            }
            // 규칙 2(고액 승인)가 여기 없다 — 복제가 드리프트했다
            order.setStatus("CANCELLED");
        }
    }
}
