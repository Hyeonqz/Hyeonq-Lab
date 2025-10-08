package org.hyeonqz.kafkalab.presentation;

import lombok.RequiredArgsConstructor;
import org.hyeonqz.kafkalab.application.KafkaAdminClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaAdminController {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private final KafkaAdminClientService kafkaAdminClientService;

    @GetMapping("/topics")
    public ResponseEntity<?> getTopics(){
        return ResponseEntity.ok(kafkaAdminClientService.callAdminClientTopicList());
    }

    @GetMapping("/consumerGroups")
    public ResponseEntity<Void> getConsumerGroups() throws Exception {
        kafkaAdminClientService.callConsumerGroupsList();
        return ResponseEntity.ok().build();
    }

}
