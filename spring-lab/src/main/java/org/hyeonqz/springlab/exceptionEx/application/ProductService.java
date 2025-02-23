package org.hyeonqz.springlab.exceptionEx.application;

import org.hyeonqz.springlab.exceptionEx.infrastructure.ProductRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {
	private final ProductRepository productRepository;


}
