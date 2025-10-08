package org.hyeonqz.kafkalab.application.producer;

import org.hyeonqz.kafkalab.domain.dto.LogRequestDto;
import org.hyeonqz.kafkalab.shared.message.KafkaMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LogProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private LogProducer logProducer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        logProducer = new LogProducer(kafkaTemplate);
        ReflectionTestUtils.setField(logProducer, "topic", "test-logs-topic");
    }

    @Test
    @DisplayName("produceMessage should wrap dto into KafkaMessage and send to configured topic")
    void produceMessage_sendsKafkaMessage() {
        // given
        LogRequestDto dto = new LogRequestDto(
                "/api/test",
                "127.0.0.1",
                "POST",
                "{User-Agent:JUnit}",
                Map.of("key", "value")
        );

        ArgumentCaptor<KafkaMessage> messageCaptor = ArgumentCaptor.forClass(KafkaMessage.class);

        // when
        logProducer.produceMessage(dto);

        // then
        verify(kafkaTemplate, times(1)).send(eq("test-logs-topic"), messageCaptor.capture());

        KafkaMessage<?> sent = messageCaptor.getValue();
        assertThat(sent).isNotNull();
        assertThat(sent.getEventType()).isEqualTo("batch");
        assertThat(sent.getPayload()).isInstanceOf(LogRequestDto.class);
        assertThat(((LogRequestDto) sent.getPayload()).requestUrl()).isEqualTo(dto.requestUrl());
        assertThat(((LogRequestDto) sent.getPayload()).requestIp()).isEqualTo(dto.requestIp());
        assertThat(((LogRequestDto) sent.getPayload()).requestMethod()).isEqualTo(dto.requestMethod());
        assertThat(((LogRequestDto) sent.getPayload()).requestHeader()).isEqualTo(dto.requestHeader());
        assertThat(((LogRequestDto) sent.getPayload()).requestBody()).isEqualTo(dto.requestBody());

        assertThat(sent.getCorrelationId()).isNotBlank();
        assertThat(sent.getMessageId()).isNotBlank();
        assertThat(sent.getVersion()).isEqualTo("1.0");
        assertThat(sent.getPublisher()).isEqualTo("payment-gateway-service");
        assertThat(sent.getTimestamp()).isNotNull();
    }
}
