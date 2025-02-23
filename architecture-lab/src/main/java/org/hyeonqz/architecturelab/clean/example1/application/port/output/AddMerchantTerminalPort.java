package org.hyeonqz.architecturelab.clean.example1.application.port.output;

import org.hyeonqz.architecturelab.clean.example1.domain.MerchantTerminalDomain;

@FunctionalInterface
public interface AddMerchantTerminalPort {
    MerchantTerminalDomain save(MerchantTerminalDomain merchantTerminalDomain);
}
