package org.hyeonqz.architecture.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 하나의 데이터베이스. 실제라면 MySQL이지만 요점은 저장 기술이 아니라 트랜잭션 경계다 (Step 11).
 *
 * 핵심: confirmWithOutbox()는 업무 데이터(주문)와 이벤트(아웃박스 행)를 **하나의 트랜잭션**으로 커밋한다.
 * 자바 맵으로는 원자성을 흉내낼 뿐이지만, "둘은 같은 DB에 함께 커밋된다"는 성질이 아웃박스 패턴의 전부다.
 */
public class OrderStore {

    /** 확정된 주문: orderId -> amount */
    private final Map<String, BigDecimal> orders = new ConcurrentHashMap<>();
    /** 아직 발행되지 않은 아웃박스 이벤트 */
    private final List<OutboxEvent> pendingOutbox = new ArrayList<>();

    /**
     * 이중 쓰기(dual write)를 피하는 길: 주문과 이벤트를 한 트랜잭션으로.
     * 이 메서드 안에서 프로세스가 죽으면 둘 다 없고, 성공하면 둘 다 있다 — 중간 상태는 관측되지 않는다.
     */
    public synchronized void confirmWithOutbox(String orderId, BigDecimal amount, OutboxEvent event) {
        orders.put(orderId, amount);
        pendingOutbox.add(event);
    }

    /** 아웃박스 없이 주문만 저장 (naive 경로 시연용). */
    public synchronized void confirmOnly(String orderId, BigDecimal amount) {
        orders.put(orderId, amount);
    }

    public synchronized boolean hasOrder(String orderId) {
        return orders.containsKey(orderId);
    }

    /** 릴레이가 발행할 미처리 이벤트를 가져간다. */
    public synchronized List<OutboxEvent> pollPending() {
        return new ArrayList<>(pendingOutbox);
    }

    /** 발행이 확인된 이벤트를 아웃박스에서 지운다. */
    public synchronized void markPublished(OutboxEvent event) {
        pendingOutbox.remove(event);
    }

    public synchronized int pendingCount() {
        return pendingOutbox.size();
    }
}
