package org.hyeonqz.springlab.event_example.dto;

import java.math.BigDecimal;

public record RequestDto(
	BigDecimal amount,
	String name
) {
}
