package org.hyeonqz.architecture.payment.hexagonal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.hyeonqz.architecture.payment.hexagonal.application.PaymentService;
import org.hyeonqz.architecture.payment.hexagonal.application.PaymentUseCase;
import org.hyeonqz.architecture.payment.hexagonal.domain.Money;
import org.hyeonqz.architecture.payment.hexagonal.domain.Payment;
import org.hyeonqz.architecture.payment.hexagonal.domain.PaymentRepository;
import org.junit.jupiter.api.Test;

/**
 * 헥사고날 유스케이스 — 피주도 포트에 인메모리 목을 꽂아 DB 없이 전체 흐름을 검증한다.
 * JPA 어댑터를 목으로 갈아끼울 수 있다는 것이 포트/어댑터의 이점(테스트가능성).
 */
class PaymentServiceTest {

    /** 인메모리 어댑터 — 피주도 포트의 목. */
    static class InMemoryPaymentRepository implements PaymentRepository {
        private final Map<String, Payment> store = new HashMap<>();
        public void save(Payment payment) { store.put(payment.id(), payment); }
        public Payment getById(String id) {
            Payment p = store.get(id);
            if (p == null) throw new NoSuchElementException(id);
            return p;
        }
    }

    private final InMemoryPaymentRepository repository = new InMemoryPaymentRepository();
    private final PaymentUseCase paymentUseCase = new PaymentService(repository);

    @Test
    void 승인_정산_흐름이_동작한다() {
        paymentUseCase.approve("P-1", 10000);

        Money fee = paymentUseCase.settle("P-1");

        assertEquals(0, new java.math.BigDecimal("300.00").compareTo(fee.amount()));
    }
}
