package org.hyeonqz.redislab.util;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class DateTimeUtil {

	public LocalDateTime settlementDateTime() {
		return LocalDateTime.now().plusDays(2);
	}
}
