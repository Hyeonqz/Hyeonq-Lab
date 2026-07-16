package org.hyeonqz.architecture.growth.hexagonal;

/** 피주도 포트 — 도메인 소유. */
public interface OrderRepository {
    Order byId(String orderId);
    void save(Order order);
}
