package org.hyeonqz.springlab.event_example.repository;

import java.math.BigDecimal;

import org.hyeonqz.springlab.event_example.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
