package io.github.hyeonqz.archlab.eda_arch.domainevent.order.infra;

import io.github.hyeonqz.archlab.eda_arch.domainevent.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
