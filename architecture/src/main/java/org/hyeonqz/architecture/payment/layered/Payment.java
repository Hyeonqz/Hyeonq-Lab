package org.hyeonqz.architecture.payment.layered;

import java.math.BigDecimal;

/**
 * 레이어드 — 빈혈 도메인 모델. 데이터만 있고 규칙은 서비스에 산다.
 * 금액을 원시 BigDecimal 로 다룬다(primitive obsession) — DDD 스타일과의 대비점.
 */
public class Payment {

    private String id;
    private String status; // APPROVED, CANCELLED, SETTLED
    private BigDecimal amount;
    private BigDecimal fee;

    public Payment() {
    }

    public Payment(String id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
        this.status = "APPROVED";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}
