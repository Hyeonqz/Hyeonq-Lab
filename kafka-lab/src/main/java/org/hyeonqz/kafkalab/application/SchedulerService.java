package org.hyeonqz.kafkalab.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hyeonqz.kafkalab.domain.entity.KafkaMetaData;
import org.hyeonqz.kafkalab.infra.repository.KafkaMetaRepository;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchedulerService implements DisposableBean {
    private final KafkaMetaRepository kafkaMetaRepository;

    private final List<KafkaMetaData> kafkaMetaDataList = Collections.synchronizedList(new ArrayList<>());
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);


    /*
    * Spring은 다음 순서로 종료:
    * DataSource Bean 파괴
    * JPA EntityManager 종료
    * @PreDestroy 메서드 실행 ← 이때 DB 접근 불가 -> DisposableBean 을 사용하여 Bean 초기화전에 실행한다.
    * -> IDE 에서 직접 종료하면 Kill-9 가 동작하여 로그 불가함.
    * */
    @Transactional
    @Override
    public void destroy() throws Exception {
        log.info("=== DisposableBean.destroy() 시작: kafkaMetaDataList 비우기 ===");
        isShuttingDown.set(true);

        try {
            if (!kafkaMetaDataList.isEmpty()) {
                List<KafkaMetaData> dataToSave = new ArrayList<>(kafkaMetaDataList);
                kafkaMetaRepository.saveAll(dataToSave);
                log.info("Graceful Shutdown: {} 건 저장 완료", dataToSave.size());
            } else {
                log.info("저장할 데이터가 없습니다.");
            }
        } catch (Exception e) {
            log.error("Graceful Shutdown 중 DB 저장 실패", e);
        } finally {
            kafkaMetaDataListClear();
            log.info("=== DisposableBean.destroy() 완료 ===");
        }
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
