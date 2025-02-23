package org.hyeonqz.architecturelab.clean.example1.application.port.input;

import org.hyeonqz.architecturelab.clean.example1.application.command.AddMerchantTerminalCommand;
import org.hyeonqz.architecturelab.clean.example1.domain.MerchantTerminalDomain;

@FunctionalInterface
public interface AddMerchantTerminalUseCase {
    MerchantTerminalDomain addMerchantTerminal(AddMerchantTerminalCommand command);
}
