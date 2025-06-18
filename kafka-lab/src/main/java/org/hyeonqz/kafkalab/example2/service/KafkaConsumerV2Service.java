package org.hyeonqz.kafkalab.example2.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hyeonqz.kafkalab.common.consumerService;
import org.hyeonqz.kafkalab.common.messages.KafkaMessage;
import org.hyeonqz.kafkalab.example2.entity.KafkaMetaData;
import org.hyeonqz.kafkalab.example2.repository.KafkaMetaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumerV2Service implements consumerService<List<ConsumerRecord<String, KafkaMessage>>> {
    private final KafkaMetaRepository kafkaMetaRepository;

    @KafkaListener(
        id = "payment-audit",
        containerFactory = "batchKafkaListenerContainerFactory",
        groupId = "${hkjin.kafka.topics.audit.group-id}",
        topicPartitions = @TopicPartition(
            topic = "${hkjin.kafka.topics.audit.name}",
            partitions = {"0"}
        ),
        concurrency = "1"
    )
    @Transactional
    @Override
    public void consumeMessage (List<ConsumerRecord<String, KafkaMessage>> records, Acknowledgment acknowledgment) {
        log.info("Payment Audit - Partition 1 Messages: {}", records.size());

        Set<Integer> partitions = records.stream()
            .map(ConsumerRecord::partition)
            .collect(Collectors.toSet());
        log.info("Partitions: {}", partitions);

        List<KafkaMetaData> list = records.stream()
            .map(this::createKafkaMetaData)
            .toList();

        boolean isFull = records.size() == 100;
        String trigger = isFull ? "MAX_POLL_RECORDS" : "FETCH_MAX_WAIT_MS";

        kafkaMetaRepository.saveAll(list);
        acknowledgment.acknowledge();
        log.info("Success Processing Audit Batch: {}, trigger: {}", records.size(), trigger);
    }

    private KafkaMetaData createKafkaMetaData (ConsumerRecord<String, KafkaMessage> record) {
        return KafkaMetaData.builder()
            .topics(record.topic())
            .partitions(String.valueOf(record.partition()))
            .offset(record.offset())
            .key(record.key())
            .value(record.value().toString())
            .createTime(record.value().timeStamp())
            .consumerTime(LocalDateTime.now())
            .publishedTime(record.value().publishedTime())
            .build();
    }

}
