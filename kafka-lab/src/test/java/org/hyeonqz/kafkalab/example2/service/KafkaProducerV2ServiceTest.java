package org.hyeonqz.kafkalab.example2.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KafkaProducerV2ServiceTest {

    @Autowired
    private KafkaProducerV2Service kafkaProducerV2Service;

    @Test
    @DisplayName("지정된 파티션에 메세지를 발행한다.")
    void produceMessage () {
        // when & then
        kafkaProducerV2Service.produceMessage();
    }

}