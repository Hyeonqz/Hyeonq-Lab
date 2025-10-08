package org.hyeonqz.kafkalab.config.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value(value="${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // 1. broker 관련 설정
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress); // Producer 가 처음으로 연결할 Kafka 브로커 위치를 설정한다.
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, "kafka-producer"); // producer 와 그것을 사용하는 애플리케이션을 구분하기 위한 논리적 식별자
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // 프로듀서가 쓰기 작업 성공 판별시 얼마나 많은 파티션 레플리카가 해당 레코드를 받아야 하는지 결정 -> all 설정시 메시지 유실 거의 없음

        // 2. 메시지 전달 시간 관련
        configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000); // send() 시 프로듀서가 얼마나 오랫동안 블록되는지 결정
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // 레코즈 전송 준비가 완료된 시점 이후 브로커의 응답을 받거나 아니면 전송 포기 되는 시점까지의 제한시간을 정한다.
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 프로듀서가 데이터 전송 시 서버로부터 응답을 받기 위해 얼마 까지 기다릴 수 있는지 결정 (전송 포기 대기 시간까지 포함한다)
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 1000); // 현재 배치 전송하기 전까지 대기하는 시간을 결정 -> linger.ms 제한시간이 되었을 때 메시지 배치를 전송한다.
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 메시지 전송 전 메시지를 대기시키는 버퍼의 크기 결정
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none"); // 메시지를 압축해서 보내야 할 떄 (압축 성능 및 네트워크 대역폭 모두가 중요할 때 권장)
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 100000); // 같은 파티션에 다수의 레코드가 전송될 경우 배치 단위로 한꺼번에 전송한다. -> 위 매개변수는 각 배치에 사용될 메모리의 양을 결정한다(바이트)
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // 프로듀서가 서버로부터 응답을 받지 못한 상태에서 전송할 수 있는 최대 메시지 개수 결정 -> 위 값을 올리면 메모리 사용량이 증가하지만, 처리량 증가
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1000); // 프로듀서가 전송하는 쓰기 요청의 크기 결정 ex) 1000kb = 1mb

        // 2-1) 메시지 전송 실패시 재시도 관련
        // 아래 설정을 조정을 권장하지 않고, delivery.timeout.ms 를 조정하기를 권장한다!
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // 재시도 횟수
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000); // 재시도 시간 지정


        // 직렬화 메커니즘 설정
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);



        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        // Kafka Producer 를 Wrapping 한 클래스
        // Kafka 에 메시지를 보내는 여러 메소드 제공
        return new KafkaTemplate<>(producerFactory());
    }
}

