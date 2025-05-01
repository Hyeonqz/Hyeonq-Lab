package org.hyeonqz.springlab.event_example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hyeonqz.springlab.exceptionEx.domain.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Payment {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String transactionNo;

	private BigDecimal amount;

	private LocalDateTime createdAt;

	private String state;

	@OneToMany(mappedBy = "payment")
	private List<ProductItem> productItems = new ArrayList<>();

	@Builder
	public Payment (String transactionNo, BigDecimal amount, LocalDateTime createdAt, String state,
		List<ProductItem> productItems) {
		this.transactionNo = transactionNo;
		this.amount = amount;
		this.createdAt = createdAt;
		this.state = state;
		this.productItems = productItems;
	}

}
