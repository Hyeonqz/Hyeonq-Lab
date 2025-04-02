package org.hyeonqz.architecturelab.clean.example2.application.usecase;

@FunctionalInterface
public interface SendMoneyUseCase {
    boolean sendMoney(SendMoneyCommand command);
}
