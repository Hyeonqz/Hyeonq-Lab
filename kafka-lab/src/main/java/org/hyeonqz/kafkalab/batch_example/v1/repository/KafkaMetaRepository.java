package org.hyeonqz.kafkalab.batch_example.v1.repository;

import org.hyeonqz.kafkalab.batch_example.v1.entity.KafkaMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaMetaRepository extends JpaRepository<KafkaMetaData, Long> {
}
