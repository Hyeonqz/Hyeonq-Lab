package org.hyeonqz.springlab.event_example.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ProductItem {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Integer quantity;

	private LocalDateTime createAt;

	@JoinColumn(name="payment_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Payment payment;

	@Builder
	public ProductItem (String name, Integer quantity, LocalDateTime createAt, Payment payment) {
		this.name = name;
		this.quantity = quantity;
		this.createAt = createAt;
		this.payment = payment;
	}

	@Override
	public String toString () {
		return "ProductItem{" +
			"createAt=" + createAt +
			", quantity=" + quantity +
			", name='" + name + '\'' +
			", id=" + id +
			'}';
	}

}
