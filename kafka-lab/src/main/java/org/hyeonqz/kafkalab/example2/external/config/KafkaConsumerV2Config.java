package org.hyeonqz.kafkalab.example2.external.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerV2Config {

    @Value("${spring.kafka.boostrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offsetReset;

    @Bean("batchConsumerFactory")
    public ConsumerFactory<String, Object> batchConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // 배치 전용 설정
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);              // 메시지 100개 쌓이면 -> 즉시 Batch 실행
        //props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 21600000);      // 12시간 지나면 -> 강제 Batch 실행
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 30000);      // 30초 -> 강제 Batch 실행
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);        // 수동 커밋

        // 12시간 대기를 위한 타임아웃 설정
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 25200000);   // consume 폴링 시간 후 최대 시간 1시간
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 22000000);     // 6시간 10분
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);        // 1분
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 30000);     // 30초

        // 네트워크 설정
        props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 25200000);// 7시간
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);               //메모리 최소 조건

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean("batchKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory(
        @Qualifier("batchConsumerFactory") ConsumerFactory<String, Object> batchConsumerFactory,
        KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(batchConsumerFactory);
        factory.setBatchListener(true); // 배치 모드 활성화
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.getContainerProperties().setShutdownTimeout(15000L); // 15초 (여유)

        // 배치 전용 에러 핸들링 (더 관대한 재시도)
        factory.setCommonErrorHandler(new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(kafkaTemplate),
            new FixedBackOff(5000L, 3L))); // 5초 간격, 3회 재시도

        return factory;
    }
}