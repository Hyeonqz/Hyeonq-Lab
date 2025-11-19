package org.hyeonqz.springlab.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class RestClientInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        try {
            ClientHttpResponse response = execution.execute(request, body);

            // api 호출 로그 및 응답 로그 기록 (비동기)서비스 호출을 통한 DB 에 기록
            String paymentCompany = MDC.get("paymentCompany");
            log.info("paymentCompany: {}", paymentCompany);

            return response;
        } finally {
            MDC.clear();
        }
    }

}
