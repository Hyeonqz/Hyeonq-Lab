package org.hyeonqz.kafkalab.shared.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Kafka 메시지 공통 포맷
 * 모든 Kafka 메시지는 이 Envelope로 감싸져서 전송됨
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaMessage<T> {

    /**
     * 메시지 고유 ID (추적용)
     */
    private String messageId;

    /**
     * 이벤트 타입
     */
    private String eventType;

    /**
     * 이벤트 발생 시각
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * 메시지 버전 (스키마 버전 관리)
     */
    private String version;

    /**
     * 발행자 정보 (어떤 서비스/시스템에서 발행했는지)
     */
    private String publisher;

    /**
     * 상관관계 ID (분산 추적용)
     */
    private String correlationId;

    /**
     * 실제 페이로드 (비즈니스 데이터)
     */
    private T payload;


    /**
     * 메시지 생성 팩토리 메서드
     */
    public static <T> KafkaMessage<T> of(String eventType, T payload) {
        return KafkaMessage.<T>builder()
                .messageId(UUID.randomUUID().toString())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .version("1.0")
                .publisher("payment-gateway-service")
                .payload(payload)
                .build();
    }

    /**
     * 상관관계 ID를 포함한 메시지 생성
     */
    public static <T> KafkaMessage<T> of(String eventType, T payload, String correlationId) {
        return KafkaMessage.<T>builder()
                .messageId(UUID.randomUUID().toString())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .version("1.0")
                .publisher("payment-gateway-service")
                .correlationId(correlationId)
                .payload(payload)
                .build();
    }
}