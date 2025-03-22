package org.hyeonqz.java_lab.sealed;

import java.time.LocalDateTime;

public record MarketOrder() implements FXOrder {
	@Override
	public int units () {
		return 0;
	}

	@Override
	public LocalDateTime sendAt () {
		return null;
	}

}
