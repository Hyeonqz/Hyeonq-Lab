package org.hyeonqz.architecture.hexagonal.domain;

/**
 * 피주도(driven) 포트 — 도메인이 소유한 계약 (Step 03의 소유권 역전, Step 08의 포트).
 * 메서드가 SQL이 아니라 정책의 언어를 닮는다. 구현(어댑터)은 바깥에 산다.
 */
public interface OrderRepository {

    Order byId(String orderId);

    void save(Order order);
}
