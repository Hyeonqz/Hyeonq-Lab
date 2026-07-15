package org.hyeonqz.architecture.layered.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.hyeonqz.architecture.layered.entity.Order;

/**
 * 실제라면 JpaRepository + MySQL이다. 여기서는 맵으로 흉내낸다.
 * 요점은 저장 기술이 아니라 화살표다 — 서비스가 "이 구체 클래스의 이름"을 안다는 사실.
 */
public class MySqlOrderRepository {

    private final Map<String, Order> table = new HashMap<>();

    public Order findById(String id) {
        Order order = table.get(id);
        if (order == null) {
            throw new NoSuchElementException("order not found: " + id);
        }
        return order;
    }

    public void save(Order order) {
        table.put(order.getId(), order);
    }
}
