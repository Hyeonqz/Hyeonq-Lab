package org.hyeonqz.springlab.troubleshooting.ex1.aop;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

@Slf4j
@Service
public class KakaoPayService extends PaymentAbstractBaseService {

    public KakaoPayService(RestClient restClient) {
        super(restClient);
    }

    @PostConstruct
    public void checkProxyType() {
        log.info("=== Proxy Type Check ===");
        log.info("Class Name: {}", this.getClass().getName());
        log.info("Superclass: {}", this.getClass().getSuperclass().getName());
        log.info("Is CGLIB Proxy: {}", this.getClass().getName().contains("$$"));
        log.info("Interfaces: {}", Arrays.toString(this.getClass().getInterfaces()));
    }

    @Override
    @PaymentContext(value = "KakaoPay")
    public void approvalPayment() {
        sendHttpGetRequest(
                "http://localhost:9010/apis/v1/test",
                String.class
        );
    }

    @Override
    public void cancelPayment() {
        // 결제 취소 처리
    }

    @Override
    public String getSupported() {
        return "KakaoPay";
    }
}
