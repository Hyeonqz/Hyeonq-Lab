package org.hyeonqz.kafkalab.infra.repository;

import org.hyeonqz.kafkalab.domain.entity.PerformanceMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceMetricsRepository extends JpaRepository<PerformanceMetrics, Long> {
}
