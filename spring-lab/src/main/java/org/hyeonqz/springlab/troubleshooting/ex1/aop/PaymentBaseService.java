package org.hyeonqz.springlab.troubleshooting.ex1.aop;

public interface PaymentBaseService {

    void approvalPayment();

    void cancelPayment();

    String getSupported();
}
