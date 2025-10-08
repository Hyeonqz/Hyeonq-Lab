package org.hyeonqz.kafkalab.domain.dto;

import java.util.Map;

public record LogRequestDto(
        String requestUrl,
        String requestIp,
        String requestMethod,
        String requestHeader,
        Map<String, String> requestBody
) {

}
