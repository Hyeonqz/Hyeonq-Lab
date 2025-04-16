package org.hyeonqz.architecturelab.clean.example2.adapter.web;

import lombok.RequiredArgsConstructor;
import org.hyeonqz.architecturelab.clean.example2.application.port.in.SendMoneyCommand;
import org.hyeonqz.architecturelab.clean.example2.application.usecase.SendMoneyUseCase;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AccountController {
    private final SendMoneyUseCase sendMoneyUseCase;

    @PostMapping("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    public void sendMoney(
        @PathVariable("sourceAccountId") Long sourceAccountId,
        @PathVariable("targetAccountId") Long targetAccountId,
        @PathVariable("amount") Long amount
    ) {
        SendMoneyCommand command = new SendMoneyCommand(
          sourceAccountId, targetAccountId, amount
        );

        sendMoneyUseCase.sendMoney(command);
    }
}
