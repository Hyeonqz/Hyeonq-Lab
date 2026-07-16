package org.hyeonqz.architecture.payment.layered;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/** 실제라면 JpaRepository + MySQL. 요점은 저장 기술이 아니라 화살표라 맵으로 흉내낸다. */
public class PaymentRepository {

    private final Map<String, Payment> table = new HashMap<>();

    public Payment findById(String id) {
        Payment payment = table.get(id);
        if (payment == null) {
            throw new NoSuchElementException("payment not found: " + id);
        }
        return payment;
    }

    public void save(Payment payment) {
        table.put(payment.getId(), payment);
    }
}
