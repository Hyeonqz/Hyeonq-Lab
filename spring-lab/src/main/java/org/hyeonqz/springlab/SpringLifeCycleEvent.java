package org.hyeonqz.springlab;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringLifeCycleEvent {
    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed(ContextRefreshedEvent event) {
        log.info("1. ContextRefreshed - 빈 초기화 완료, 서버 시작 전");
        log.info("- 애플리케이션 컨텍스트: {}", event.getApplicationContext().getClass().getSimpleName());
    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted(ApplicationStartedEvent event) {
        log.info("2. ApplicationStarted - 서버 시작 완료, CommandLineRunner 실행 전");
        log.info("- 애플리케이션: {}", event.getSpringApplication().getClass().getSimpleName());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.info("3. ApplicationReady - 모든 준비 완료!");
        log.info("- 애플리케이션 완전 준비됨");
    }
}
