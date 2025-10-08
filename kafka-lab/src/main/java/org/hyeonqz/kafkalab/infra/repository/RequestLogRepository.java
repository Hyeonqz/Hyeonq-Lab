package org.hyeonqz.kafkalab.infra.repository;

import org.hyeonqz.kafkalab.domain.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
}
