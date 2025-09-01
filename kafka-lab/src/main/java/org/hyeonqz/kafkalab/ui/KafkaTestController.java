package org.hyeonqz.kafkalab.ui;

import org.hyeonqz.kafkalab.application.SchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class KafkaTestController {
    private final KafkaProducerV2Service kafkaProducerV2Service;
    private final SchedulerService schedulerService;


    @PostMapping("/apis/v1/produce")
    public ResponseEntity<?> produce() {
        kafkaProducerV2Service.produceMessage();

        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/apis/v1/batch/request")
    public ResponseEntity<Void> request() {
        schedulerService.doProcessKafkaMetaData();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/apis/v1/consumer/count")
    public ResponseEntity<?> getConsumerCount() {
        return ResponseEntity.ok(schedulerService.getKafkaMetaDataListCount());
    }
}
