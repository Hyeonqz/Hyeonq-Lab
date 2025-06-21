package org.hyeonqz.kafkalab.batch_example.v2.repository;

import org.hyeonqz.kafkalab.batch_example.v2.entity.ErrorTrackingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorTrackingLogRepository extends JpaRepository<ErrorTrackingLog, Long> {
}
