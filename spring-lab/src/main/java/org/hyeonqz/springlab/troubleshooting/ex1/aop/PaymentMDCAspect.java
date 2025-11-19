package org.hyeonqz.springlab.troubleshooting.ex1.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PaymentMDCAspect {

    @Around("@annotation(context)")
    public Object setPaymentContext(ProceedingJoinPoint joinPoint,
                                    PaymentContext context) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String paymentCompany = context.value();

        log.info(">>> AOP 진입: method={}, paymentCompany={}", methodName, paymentCompany);

        try {
            MDC.put("paymentCompany", paymentCompany);
            log.info(">>> MDC 설정 완료: paymentCompany={}", paymentCompany);

            return joinPoint.proceed();
        } finally {
            MDC.remove("paymentCompany");
            log.info(">>> MDC 정리 완료");
        }
    }
}