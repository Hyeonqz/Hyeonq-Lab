package org.hyeonqz.kafkalab.domain.entity;

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
public class PerformanceMetrics {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long metricId;

    @Column(nullable = false)
    private LocalDateTime measuredTime;

    @Column(nullable = false)
    private String ServiceName;

    private String apiEndpoint;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Builder
    public PerformanceMetrics (LocalDateTime measuredTime, String serviceName, String apiEndpoint,
        LocalDateTime createTime) {
        this.measuredTime = measuredTime;
        ServiceName = serviceName;
        this.apiEndpoint = apiEndpoint;
        this.createTime = createTime;
    }

    public void updateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}
