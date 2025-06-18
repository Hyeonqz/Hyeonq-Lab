package org.hyeonqz.kafkalab.example2.presentation;

import org.hyeonqz.kafkalab.example2.service.KafkaProducerV2Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class KafkaTestController {
    private final KafkaProducerV2Service kafkaProducerV2Service;


    @PostMapping("/apis/v1/produce")
    public ResponseEntity<?> produce() {
        kafkaProducerV2Service.produceMessage();

        return ResponseEntity.ok().body("Success");
    }
}
