package org.hyeonqz.architecturelab.eda.domainevent.order.infra;

import org.hyeonqz.architecturelab.eda.domainevent.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
