package org.hyeonqz.kafkalab.batch_example.v1.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hyeonqz.kafkalab.common.consumerService;
import org.hyeonqz.kafkalab.common.messages.KafkaMessage;
import org.hyeonqz.kafkalab.batch_example.v1.entity.KafkaMetaData;
import org.hyeonqz.kafkalab.batch_example.v1.repository.KafkaMetaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
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
        topics = "${hkjin.kafka.topics.audit.name}",
        concurrency = "3"
    )
    @Transactional
    @Override
    public void consumeMessage(List<ConsumerRecord<String, KafkaMessage>> records, Acknowledgment acknowledgment) {
        log.info("=== Batch Consumer Triggered ===");
        log.info("Received batch size: {}", records.size());

        // 배치 처리 실행
        this.processMessage(records);

        // kafka 수동 commit
        acknowledgment.acknowledge();
        log.info("=== Batch Processing Completed ===");
    }

    private void processMessage(List<ConsumerRecord<String, KafkaMessage>> records) {
        log.info("Payment Audit - Partition Messages Size: {}", records.size());

        Set<Integer> partitions = getPartitions(records);
        log.info("Partitions: {}", partitions);

        List<KafkaMetaData> list = getKafkaMetaDataList(records);

        // 배치 트리거 조건 확인
        String trigger = determineTrigger(records.size());

        kafkaMetaRepository.saveAll(list);

        log.info("Success Processing Audit Batch: {}, trigger: {}", records.size(), trigger);
    }

    private String determineTrigger(int batchSize) {
        if (batchSize >= 3)
            return "MAX_POLL_RECORDS"; // 3개 이상이면 크기 기준
        else
            return "FETCH_MAX_WAIT_MS"; // 6시간 타임아웃 기준
    }

    private Set<Integer> getPartitions(List<ConsumerRecord<String, KafkaMessage>> records) {
        return records.stream()
            .map(ConsumerRecord::partition)
            .collect(Collectors.toSet());
    }

    private List<KafkaMetaData> getKafkaMetaDataList(List<ConsumerRecord<String, KafkaMessage>> records) {
        return records.stream()
            .map(this::createKafkaMetaData)
            .toList();
    }

    private KafkaMetaData createKafkaMetaData(ConsumerRecord<String, KafkaMessage> record) {
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