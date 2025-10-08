package org.hyeonqz.kafkalab.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Spring Kafka가 제공하는 KafkaAdmin Bean
     * 애플리케이션 시작 시 자동으로 토픽 생성 등의 작업 수행
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // DNS 별칭을 사용할 경우, 2개 이상의 IP주소로 연결되는 하나의 DNS 항목을 사용할 경우 사용
        configs.put(AdminClientConfig.CLIENT_DNS_LOOKUP_CONFIG, "use_all_dns_ips");

        // 연결 안정성
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 어플리케이션이 AdminClient 응답 기다릴 수 있는 최대 시간 정의

        // 재시도 설정
        configs.put(AdminClientConfig.RETRIES_CONFIG, 3);
        configs.put(AdminClientConfig.RETRY_BACKOFF_MS_CONFIG, 1000);

        // 클라이언트 ID
        configs.put(AdminClientConfig.CLIENT_ID_CONFIG, "payment-gateway-admin");

        return new KafkaAdmin(configs);
    }

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        return AdminClient.create(configs);
    }
}
