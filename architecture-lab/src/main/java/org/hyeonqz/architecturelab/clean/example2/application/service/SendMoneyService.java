package org.hyeonqz.architecturelab.clean.example2.application.service;

import lombok.RequiredArgsConstructor;
import org.hyeonqz.architecturelab.clean.example2.application.usecase.SendMoneyUseCase;

@RequiredArgsConstructor
public class SendMoneyService implements SendMoneyUseCase {

    @Override
    public boolean sendMoney(SendMoneyCommand command) {
        return false;
    }
}
