package org.hyeonqz.springlab;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 초기화 방식별 실행 시점과 특성 비교
 */
@Slf4j
@Component
public class InitializationComparison {

    /**
     * 1. ContextRefreshedEvent - 가장 빠른 시점
     * - 모든 빈이 생성되고 초기화된 직후
     * - 웹 서버 시작 전
     * - 데이터베이스 연결, 캐시 초기화 등에 적합
     */
    @EventListener
    @Order(1)
    public void onContextRefreshed(ContextRefreshedEvent event) {
        log.info("📦 [이벤트] ContextRefreshed - 빈 초기화 완료");
        log.info("   - 시각: {}", LocalDateTime.now());
        log.info("   - 스레드: {}", Thread.currentThread().getName());
        log.info("   - 용도: 데이터베이스 연결, 캐시 초기화, 설정 검증");
    }

    /**
     * 2. ApplicationStartedEvent - 중간 시점
     * - 웹 서버 시작 완료
     * - CommandLineRunner 실행 전
     * - 외부 시스템 연결, API 클라이언트 초기화에 적합
     */
    @EventListener
    @Order(2)
    public void onApplicationStarted(ApplicationStartedEvent event) {
        log.info("🌐 [이벤트] ApplicationStarted - 웹 서버 시작 완료");
        log.info("   - 시각: {}", LocalDateTime.now());
        log.info("   - 스레드: {}", Thread.currentThread().getName());
        log.info("   - 용도: 외부 API 연결, 스케줄러 시작, 모니터링 시작");
    }

    /**
     * 3. ApplicationReadyEvent - 가장 늦은 시점
     * - 모든 준비 완료, 요청 처리 가능
     * - 워밍업, 헬스체크, 알림 발송에 적합
     */
    @EventListener
    @Order(3)
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.info("✅ [이벤트] ApplicationReady - 완전 준비 완료");
        log.info("   - 시각: {}", LocalDateTime.now());
        log.info("   - 스레드: {}", Thread.currentThread().getName());
        log.info("   - 용도: 캐시 워밍업, 헬스체크, 시작 알림");
    }
}
