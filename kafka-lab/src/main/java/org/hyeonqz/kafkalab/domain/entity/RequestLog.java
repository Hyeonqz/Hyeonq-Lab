package org.hyeonqz.kafkalab.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(indexes = {@Index(name = "idx_request_log_correlation_id", columnList = "correlation_id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "request_log_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String requestUrl;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String requestIp;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String requestMethod;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String correlationId;

    @Lob
    @Column(nullable = false)
    private String requestHeader;

    @Lob
    @Column(nullable = false)
    private String requestBody;

    @Builder
    public RequestLog(String requestUrl, String requestIp, String requestMethod, String correlationId, String requestHeader, String requestBody) {
        this.requestUrl = requestUrl;
        this.requestIp = requestIp;
        this.requestMethod = requestMethod;
        this.correlationId = correlationId;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

}
