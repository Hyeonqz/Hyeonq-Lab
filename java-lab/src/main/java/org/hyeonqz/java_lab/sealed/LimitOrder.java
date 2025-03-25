package org.hyeonqz.java_lab.sealed;

import java.time.LocalDateTime;

public record LimitOrder() implements FXOrder {
	@Override
	public int units () {
		return 0;
	}

	@Override
	public LocalDateTime sendAt () {
		return null;
	}

}
