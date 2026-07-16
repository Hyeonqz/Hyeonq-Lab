package org.hyeonqz.architecture.payment.layered;

import java.math.BigDecimal;

/**
 * 트랜잭션 스크립트 — 승인/취소/정산의 모든 규칙이 여기에 산다.
 * 화살표: 서비스(정책) → PaymentRepository(세부사항). 정책이 세부사항의 이름을 안다.
 */
public class PaymentService {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.03");

    private final PaymentRepository paymentRepository = new PaymentRepository();

    /** 승인: 금액이 양수면 APPROVED 로 생성한다. */
    public void approve(String paymentId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("승인 금액은 양수여야 한다: " + amount);
        }
        paymentRepository.save(new Payment(paymentId, amount));
    }

    /** 취소: 승인 상태에서만 가능하다. */
    public void cancel(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (!"APPROVED".equals(payment.getStatus())) { // 규칙이 서비스에
            throw new IllegalStateException("승인 상태에서만 취소할 수 있다: " + payment.getStatus());
        }
        payment.setStatus("CANCELLED");
        paymentRepository.save(payment);
    }

    /** 정산: 승인 상태에서만 가능하고, 수수료(3%)를 계산한다. */
    public BigDecimal settle(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (!"APPROVED".equals(payment.getStatus())) {
            throw new IllegalStateException("승인 상태에서만 정산할 수 있다: " + payment.getStatus());
        }
        payment.setStatus("SETTLED");
        BigDecimal fee = payment.getAmount().multiply(FEE_RATE); // 수수료 규칙도 서비스에
        payment.setFee(fee);
        paymentRepository.save(payment);
        return fee;
    }
}
