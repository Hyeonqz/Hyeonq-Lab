package org.hyeonqz.java_lab.sealed;

import java.time.LocalDateTime;

public sealed interface FXOrder permits MarketOrder, LimitOrder {
	int units();
	LocalDateTime sendAt();

}
