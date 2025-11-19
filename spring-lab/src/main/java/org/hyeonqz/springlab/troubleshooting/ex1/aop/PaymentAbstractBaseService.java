package org.hyeonqz.springlab.troubleshooting.ex1.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Slf4j
public abstract class PaymentAbstractBaseService implements PaymentBaseService{
    private final RestClient restClient;

    protected PaymentAbstractBaseService(RestClient restClient) {
        this.restClient = restClient;
    }

    protected <T> T sendHttpGetRequest(String url, Class<T> responseType ) {
        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(responseType)
                    ;

        } catch (Exception ex) {
            log.error("Exception occurred while calling payment service", ex);
            throw ex;
        }
    }

}

