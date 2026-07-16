package org.hyeonqz.architecture.growth.hexagonal;

import java.util.HashMap;
import java.util.Map;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> store = new HashMap<>();

    @Override
    public Order byId(String orderId) {
        return store.get(orderId);
    }

    @Override
    public void save(Order order) {
        store.put(order.id(), order);
    }
}
