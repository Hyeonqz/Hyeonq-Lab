package org.hyeonqz.architecture.growth.layered;

/** 나중에 추가된 규칙: 고액 주문 취소에는 승인이 필요하다. */
public class ApprovalRequiredException extends RuntimeException {
    public ApprovalRequiredException(String orderId) {
        super("고액 주문 취소에는 승인이 필요하다: " + orderId);
    }
}
