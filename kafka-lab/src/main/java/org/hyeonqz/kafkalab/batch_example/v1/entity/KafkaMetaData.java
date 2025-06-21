package org.hyeonqz.kafkalab.batch_example.v1.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class KafkaMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kafka_topics", nullable = false, columnDefinition = "VARCHAR(255)")
    private String topics;

    @Column(name = "kafka_partitions", nullable = false, columnDefinition = "VARCHAR(255)")
    private String partitions;

    @Column(name = "kafka_offset", nullable = false)
    private Long offset;

    @Column(name = "kafka_key", nullable = false, columnDefinition = "VARCHAR(255)")
    private String key;

    @Column(name = "kakfa_value", nullable = false, columnDefinition = "VARCHAR(255)")
    private String value;

    private LocalDateTime createTime;
    private LocalDateTime publishedTime;
    private LocalDateTime consumerTime;

    @Builder
    public KafkaMetaData (String topics, String partitions, Long offset, String key, String value,
        LocalDateTime createTime,
        LocalDateTime publishedTime, LocalDateTime consumerTime) {
        this.topics = topics;
        this.partitions = partitions;
        this.offset = offset;
        this.key = key;
        this.value = value;
        this.createTime = createTime;
        this.publishedTime = publishedTime;
        this.consumerTime = consumerTime;
    }

}
