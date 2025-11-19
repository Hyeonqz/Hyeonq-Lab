package org.hyeonqz.springlab.troubleshooting.ex1.aop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest
class KakaoPayServiceTest {

    @Autowired
    private KakaoPayService kakaoPayService;

    @Autowired
    private RestClient restClient;

    @Test
    @DisplayName("")
    void KakaoPayServiceTest() {
        // given

        // when
        kakaoPayService.approvalPayment();

        // then
    }

}