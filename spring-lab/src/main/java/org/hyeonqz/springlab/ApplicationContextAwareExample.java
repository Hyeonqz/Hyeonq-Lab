package org.hyeonqz.springlab;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ApplicationContextAware 방식의 초기화 예제
 * - 빈 생성 과정에서 동기적으로 실행
 * - ApplicationContext에 직접 접근 가능
 */
@Slf4j
@Component
public class ApplicationContextAwareExample implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        
        log.info("🔧 ApplicationContextAware.setApplicationContext() 호출");
        log.info("   - 현재 시각: {}", LocalDateTime.now());
        log.info("   - 스레드: {}", Thread.currentThread().getName());
        log.info("   - 빈 개수: {}", applicationContext.getBeanDefinitionCount());
        log.info("   - ApplicationContext 타입: {}", applicationContext.getClass().getSimpleName());
        
        // ApplicationContext를 통해 다른 빈에 접근 가능
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        log.info("   - 등록된 빈 중 일부: {}", 
            java.util.Arrays.stream(beanNames)
                .filter(name -> name.contains("springlab"))
                .limit(3)
                .toArray());
    }

    /**
     * ApplicationContext를 활용한 초기화 로직 예제
     */
    public void performInitialization() {
        if (applicationContext != null) {
            log.info("🚀 ApplicationContextAware 기반 초기화 로직 실행");
            // 여기서 필요한 초기화 작업 수행
        }
    }
}
