package org.hyeonqz.kafkalab.infra.repository;

import org.hyeonqz.kafkalab.domain.entity.KafkaMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaMetaRepository extends JpaRepository<KafkaMetaData, Long> {
}
