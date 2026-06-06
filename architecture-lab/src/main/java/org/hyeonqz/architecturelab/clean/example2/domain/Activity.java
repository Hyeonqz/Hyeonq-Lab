package org.hyeonqz.architecturelab.clean.example2.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Activity {
    private final AccountId ownerAccountId;
    private final AccountId sourceAccountId;
    private final AccountId targetAccountId;
    private final LocalDateTime timestamp;
    private final Money money;

    public Activity(AccountId ownerAccountId, AccountId sourceAccountId,
                    AccountId targetAccountId, LocalDateTime timestamp, Money money) {
        this.ownerAccountId = ownerAccountId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.timestamp = timestamp;
        this.money = money;
    }
}
