package org.hyeonqz.architecture.hexagonal.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.hyeonqz.architecture.hexagonal.domain.Order;
import org.hyeonqz.architecture.hexagonal.domain.OrderRepository;

/**
 * 피주도 어댑터 — 포트의 계약을 특정 기술로 번역한다.
 * MySQL로 바꾸고 싶으면 이 파일 "옆에" MySqlOrderRepository를 하나 더 만들면 된다. 도메인은 모른다.
 */
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> store = new HashMap<>();

    @Override
    public Order byId(String orderId) {
        Order order = store.get(orderId);
        if (order == null) {
            throw new NoSuchElementException("order not found: " + orderId);
        }
        return order;
    }

    @Override
    public void save(Order order) {
        store.put(order.id(), order);
    }
}
