package org.hyeonqz.kafkalab.batch_example.v1.external.config;

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
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import jakarta.annotation.PreDestroy;

@Configuration
public class KafkaConsumerV2Config {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offsetReset;

    @Bean("batchConsumerFactory")
    public ConsumerFactory<String, Object> batchConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // ✅ 3개 배치 처리에 최적화된 설정
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);           // 한 번에 최대 3개만 가져옴
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);            // 최소 바이트 수
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 21600000);   // 6시간 (21600000ms)

        // ✅ 안정적인 타임아웃 설정 (브로커 연결 끊김 방지)
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 22000000); // 6시간 + 여유시간
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);      // 60초 (기본값보다 길게)
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 20000);   // 20초

        // ✅ 연결 안정성 개선
        props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 22000000); // 6시간 + 여유시간
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60000);         // 60초

        // ✅ 수동 커밋
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean("batchKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory(
        @Qualifier("batchConsumerFactory") ConsumerFactory<String, Object> batchConsumerFactory,
        KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(batchConsumerFactory);
        factory.setBatchListener(true);

        // ✅ 배치 처리 전용 설정
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

        // ✅ 핵심: poll 간격을 6시간으로 설정
        factory.getContainerProperties().setIdleBetweenPolls(21600000L);    // 6시간
        factory.getContainerProperties().setPollTimeout(21700000L);        // 6시간 + 여유시간

        // ✅ 배치 크기 강제 설정 (Spring Kafka 2.8+)
        factory.setBatchMessageConverter(new BatchMessagingMessageConverter());

        factory.setCommonErrorHandler(new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(kafkaTemplate),
            new FixedBackOff(10000L, 3L)));

        return factory;
    }
}