package org.hyeonqz.architecturelab.clean.example2.application.usecase;

import org.hyeonqz.architecturelab.clean.example2.application.port.in.SendMoneyCommand;

@FunctionalInterface
public interface SendMoneyUseCase {
    boolean sendMoney(SendMoneyCommand command);
}
