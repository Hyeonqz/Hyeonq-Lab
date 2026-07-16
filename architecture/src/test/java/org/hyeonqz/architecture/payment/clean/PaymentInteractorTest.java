package org.hyeonqz.architecture.payment.clean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.hyeonqz.architecture.payment.clean.entity.Payment;
import org.hyeonqz.architecture.payment.clean.usecase.PaymentGateway;
import org.hyeonqz.architecture.payment.clean.usecase.PaymentInteractor;
import org.hyeonqz.architecture.payment.clean.usecase.PaymentResponse;
import org.junit.jupiter.api.Test;

/**
 * 클린 — 인터랙터를 인메모리 게이트웨이(가짜 구현)로 검증한다.
 * 게이트웨이가 추상이므로 DB 없이 유스케이스 전체가 돌아간다(헥사고날의 포트/목과 같은 이점).
 */
class PaymentInteractorTest {

    /** 테스트용 게이트웨이 구현 — 클린의 데이터 접근 경계를 맵으로. */
    static class InMemoryGateway implements PaymentGateway {
        private final Map<String, Payment> store = new HashMap<>();
        public void save(Payment payment) { store.put(payment.id(), payment); }
        public Payment getById(String id) {
            Payment p = store.get(id);
            if (p == null) throw new NoSuchElementException(id);
            return p;
        }
    }

    private final PaymentInteractor interactor = new PaymentInteractor(new InMemoryGateway());

    @Test
    void 승인하면_응답모델로_APPROVED_가_나온다() {
        PaymentResponse response = interactor.approve("P-1", 10000);

        assertEquals("APPROVED", response.status());
    }

    @Test
    void 정산하면_수수료_3퍼센트가_응답에_담긴다() {
        interactor.approve("P-1", 10000);

        PaymentResponse response = interactor.settle("P-1");

        assertEquals("SETTLED", response.status());
        assertEquals(0, new java.math.BigDecimal("300.00").compareTo(response.fee()));
    }

    @Test
    void 정산_완료된_결제는_취소할_수_없다() {
        interactor.approve("P-1", 10000);
        interactor.settle("P-1");

        assertThrows(IllegalStateException.class, () -> interactor.cancel("P-1"));
    }
}
