package org.hyeonqz.architecturelab.clean.example2.application.port.in;

public record SendMoneyCommand(
        AccountId sourceAccountId,
        AccountId targetAccountId,
        Money money
) {
    public SendMoneyCommand(AccountId sourceAccountId, AccountId targetAccountId, Money money) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;

        requireNonNull(sourceAccountId);
        requireNonNull(targetAccountId);
        requireNonNull(money);
        requireGreaterThan(money,0);

    }
}
