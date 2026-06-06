package org.hyeonqz.architecturelab.eda.applicationevent.order;

import lombok.Getter;

@Getter
public class AppEventOrder {
    private final Long id;
    private final String customerId;
    private final Long amount;

    private AppEventOrder(Long id, String customerId, Long amount) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
    }

    public static AppEventOrder create(String customerId, Long amount) {
        return new AppEventOrder(System.currentTimeMillis(), customerId, amount);
    }
}
