package org.hyeonqz.architecture.layered.entity;

import java.math.BigDecimal;

/**
 * 빈혈 도메인 모델 — 데이터 자루. 규칙이 없다.
 * setter가 열려 있으므로 이 객체는 어떤 불변식도 강제할 수 없다 (Step 06).
 */
public class Order {

    private String id;
    private String status; // CREATED, SETTLED, CANCELLED
    private BigDecimal amount;

    public Order() {
    }

    public Order(String id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
        this.status = "CREATED";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
