package org.hyeonqz.architecture.data;

import java.math.BigDecimal;

/**
 * 발신함(outbox)에 담기는 이벤트. eventId가 멱등성의 열쇠다 (Step 11).
 * 실제라면 payload는 JSON이겠지만, 요점은 직렬화가 아니라 "업무 데이터와 같은 트랜잭션에 담긴다"는 것.
 */
public record OutboxEvent(String eventId, String type, String orderId, BigDecimal amount) {
}
