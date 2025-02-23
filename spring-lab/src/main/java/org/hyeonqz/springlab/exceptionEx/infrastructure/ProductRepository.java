package org.hyeonqz.springlab.exceptionEx.infrastructure;

import org.hyeonqz.springlab.exceptionEx.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
