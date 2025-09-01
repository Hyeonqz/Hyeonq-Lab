package org.hyeonqz.kafkalab.infra.repository;

import org.hyeonqz.kafkalab.domain.entity.ErrorTrackingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorTrackingLogRepository extends JpaRepository<ErrorTrackingLog, Long> {
}
