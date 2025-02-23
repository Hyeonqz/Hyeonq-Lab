package org.hyeonqz.architecturelab.clean.example1.application.service;

import org.hyeonqz.architecturelab.clean.example1.application.command.AddMerchantTerminalCommand;
import org.hyeonqz.architecturelab.clean.example1.application.port.input.AddMerchantTerminalUseCase;
import org.hyeonqz.architecturelab.clean.example1.application.port.output.AddMerchantTerminalPort;
import org.hyeonqz.architecturelab.clean.example1.domain.MerchantTerminalDomain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AddMerchantTerminalService implements AddMerchantTerminalUseCase {

    private final AddMerchantTerminalPort addMerchantTerminalPort;

    @Override
    public MerchantTerminalDomain addMerchantTerminal(AddMerchantTerminalCommand command) {

        // 비즈니스 로직 구현

        return null;
    }

}
