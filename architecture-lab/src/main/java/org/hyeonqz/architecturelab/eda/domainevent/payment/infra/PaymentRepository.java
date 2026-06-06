package org.hyeonqz.architecturelab.eda.domainevent.payment.infra;

import org.hyeonqz.architecturelab.eda.domainevent.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
