package org.hyeonqz.kafkalab.batch_example.v2.repository;

import org.hyeonqz.kafkalab.batch_example.v2.entity.PerformanceMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceMetricsRepository extends JpaRepository<PerformanceMetrics, Long> {
}
