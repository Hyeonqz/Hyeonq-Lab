package org.hyeonqz.architecture.growth.layered;

import java.math.BigDecimal;

/**
 * 시나리오 C — 규칙이 쌓이는 세계의 레이어드 버전.
 * 여전히 빈혈이다: 데이터만 있고 규칙은 서비스에 산다.
 */
public class Order {

    private String id;
    private String status; // CREATED, SETTLED, CANCELLED
    private BigDecimal amount;

    public Order(String id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
        this.status = "CREATED";
    }

    public String getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getAmount() { return amount; }
}
