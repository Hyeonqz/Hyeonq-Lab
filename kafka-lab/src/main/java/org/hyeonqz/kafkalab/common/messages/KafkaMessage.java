package org.hyeonqz.kafkalab.common.messages;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KafkaMessage(
    @JsonProperty("message") String message,
    @JsonProperty("title") String title,
    @JsonProperty("time_stamp") LocalDateTime timeStamp,
    @JsonProperty("published_time") LocalDateTime publishedTime
) {
}
