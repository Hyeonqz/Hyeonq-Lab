package org.hyeonqz.springlab.event_example.repository;

import java.math.BigDecimal;

import org.hyeonqz.springlab.event_example.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	void updateState (BigDecimal amount);
}
