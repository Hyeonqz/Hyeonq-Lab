package org.hyeonqz.kafkalab.example2.repository;

import org.hyeonqz.kafkalab.example2.entity.KafkaMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaMetaRepository extends JpaRepository<KafkaMetaData, Long> {
}
