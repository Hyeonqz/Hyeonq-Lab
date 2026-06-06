package org.hyeonqz.architecturelab.clean.example2.application.port.in;

import org.hyeonqz.architecturelab.clean.example2.domain.AccountId;
import org.hyeonqz.architecturelab.clean.example2.domain.Money;

import java.util.Objects;

public record SendMoneyCommand(
        AccountId sourceAccountId,
        AccountId targetAccountId,
        Money money
) {
    public SendMoneyCommand {
        Objects.requireNonNull(sourceAccountId);
        Objects.requireNonNull(targetAccountId);
        Objects.requireNonNull(money);
        if (!money.isGreaterThan(0)) {
            throw new IllegalArgumentException("money must be greater than 0");
        }
    }
}
