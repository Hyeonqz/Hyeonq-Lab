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
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 직렬화 & 역직렬화
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // 배치 처리 설정
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);           // 한 번에 최대 500개 가져옴
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);            // 최소 가져올 바이트 수
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 30000);   // broker 에서 fetch_min_bytes 만큼 데이터가 쌓일 때 까지 최대 대기 시간

        // ✅ 안정적인 타임아웃 설정 (브로커 연결 끊김 방지)

        /* 이 시간동안 poll 수행하지 않으면 consumer 죽었다고 판단함. */
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 브로커랑 잘 연결되었는지 체크 시간 -> 6시간 + 여유시간
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);      // 60초 (기본값보다 길게)
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);   // 브로커 살아있는지 체크 시간 -> 20초  -> 세션 타임아웃은 1/3 으로 설정

        // ✅ 연결 안정성 개선
        props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 540000);   // 9분 + 여유시간
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);         // 30초

        // ✅ 수동 커밋
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * KafkaListener 설정
     * @param kafkaTemplate: KafkaTemplate<String, Object></String,>
     * @return ConcurrentKafkaListenerContainerFactory<String, Object></String,>
     */
    @Bean("batchKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory(KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        // ✅ 배치 처리 전용 설정
        factory.setConsumerFactory(batchConsumerFactory());
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);


        // Consumer 가 poll 시 빈값을 받았을 때 다음 poll 까지 대기 시간 -> broker 에 메시지가 없을 때만 적용되는 유휴 대기 시간 -> 메시지 있으면 즉시 poll
        factory.getContainerProperties().setIdleBetweenPolls(1000L);    // poll 사이에 대기 시간 -> 5초
        factory.getContainerProperties().setPollTimeout(65000L);        // poll 대기 시간  -> 65초
        factory.getContainerProperties().setIdleEventInterval(60000L);  // Event 일으키는 주기 -> Record fetch 이후 부터 시작 -> 1분

        // ✅ 배치 크기 강제 설정 (Spring Kafka 2.8+)
        factory.setBatchMessageConverter(new BatchMessagingMessageConverter());

        factory.setCommonErrorHandler(new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(kafkaTemplate),
            new FixedBackOff(10000L, 3L)));

        return factory;
    }
}