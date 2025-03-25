package org.hyeonqz.springlab.event_example.repository;

import org.hyeonqz.springlab.event_example.entity.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionItemRepository extends JpaRepository<ProductItem, Long> {
}
