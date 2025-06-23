package org.hyeonqz.kafkalab.batch_example.v1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hyeonqz.kafkalab.batch_example.v1.entity.KafkaMetaData;
import org.hyeonqz.kafkalab.batch_example.v1.repository.KafkaMetaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SchedulerService {
    private final KafkaMetaRepository kafkaMetaRepository;

    private final List<KafkaMetaData> kafkaMetaDataList = Collections.synchronizedList(new ArrayList<>());
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    @PreDestroy
    @Transactional
    public void preDestroy() {
        log.info("=== PreDestroy 시작 kafkaMetaDataList 비우기 로직 실행");
        isShuttingDown.set(true);
        kafkaMetaRepository.saveAll(kafkaMetaDataList);
        kafkaMetaDataListClear();
        log.info("kafkaMetaDataList Clear() 완료");
    }

    @Scheduled(cron = "0 50 23 * * *", zone = "Asia/Seoul")
    public void doProcessKafkaMetaData() {
        if (isShuttingDown.get()) {
            log.warn("서버 종료 중이므로 스케줄 작업 스킵");
            return;
        }

        flushedPendingData();
    }

    @Transactional
    public void flushedPendingData() {
        if(kafkaMetaDataList.isEmpty()) {
            log.info("처리할 데이터가 없습니다");
            return;
        }

        List<KafkaMetaData> processList = new ArrayList<>(kafkaMetaDataList);

        try {
            kafkaMetaRepository.saveAll(processList);
            kafkaMetaDataListClear();
            log.info("kafkaMetaDataList Count : [{}]", kafkaMetaDataList.size());
        } catch (Exception e) {
            throw new RuntimeException("Kafka Consumer Batch Failed", e);
        }

    }

    public synchronized void addList(List<KafkaMetaData> list) {
        if (isShuttingDown.get()) {
            log.warn("서버 종료 중이므로 스케줄 작업 스킵");
            return;
        }

        kafkaMetaDataList.add(list.get(0));
    }

    public int getKafkaMetaDataListCount() {
        return kafkaMetaDataList.size();
    }

    private void kafkaMetaDataListClear() {
        kafkaMetaDataList.clear();
    }

}
