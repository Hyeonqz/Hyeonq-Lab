package io.github.hyeonqz.archlab.eda_arch.domainevent.payment.infra;

import io.github.hyeonqz.archlab.eda_arch.domainevent.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
