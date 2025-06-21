package org.hyeonqz.kafkalab.batch_example.v2.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
@Entity
public class ErrorTrackingLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long errorTrackingId;

    @Column(nullable = false)
    private String errorCode;

    @Column(nullable = false)
    private String errorCategory;

    @Column(nullable = false)
    private String severityLevel;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String exceptionClass;

    @Column(nullable = false, columnDefinition = "text")
    private String stackTrace;

    @Column(nullable = false, columnDefinition = "text")
    private String rootCause;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal financialImpact;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Builder
    public ErrorTrackingLog (String errorCode, String errorCategory, String severityLevel, String exceptionClass,
        String stackTrace, String rootCause, BigDecimal financialImpact, LocalDateTime createTime) {
        this.errorCode = errorCode;
        this.errorCategory = errorCategory;
        this.severityLevel = severityLevel;
        this.exceptionClass = exceptionClass;
        this.stackTrace = stackTrace;
        this.rootCause = rootCause;
        this.financialImpact = financialImpact;
        this.createTime = createTime;
    }

    public void updateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}
