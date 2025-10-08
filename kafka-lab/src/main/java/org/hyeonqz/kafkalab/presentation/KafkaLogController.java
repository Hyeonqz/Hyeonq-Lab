package org.hyeonqz.kafkalab.presentation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hyeonqz.kafkalab.domain.dto.LogRequestDto;
import org.hyeonqz.kafkalab.domain.service.ProduceService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequestMapping("/apis")
@RestController
public class KafkaLogController {
    private final ProduceService<LogRequestDto> produceService;

    public KafkaLogController(@Qualifier("logProducer") ProduceService<LogRequestDto> produceService) {
        this.produceService = produceService;
    }

    @PostMapping("/log")
    public ResponseEntity<?> log(HttpServletRequest request){

        LogRequestDto message = new LogRequestDto(
                request.getRequestURI(),
                request.getRemoteUser(),
                request.getMethod(),
                request.getHeader("Authorization"),
                Map.of(
                        "test1","hello",
                        "test2","hello2"
                )
        );

        try {
            produceService.produceMessage(message);

            return ResponseEntity.ok().build();
        } catch (Exception e){
            log.error("Kafka Processing Error", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
