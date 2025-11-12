package org.hyeonqz.springlab.thread_example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncExampleService {


    @Async
    public void integrationAsync() {
        log.info("SimpleAsyncTaskExecutor 사용");
        log.info("매번 스레드 생성 후 폐기를 진행한다");
        log.info("즉 효율이 좋지 않음");
        log.info("========================");

        asyncMethod1();
        eventAsyncMethod();
    }

    @Async(value = "customExecutor")
    public void asyncMethod1() {
        log.info("Bean 으로 생성한 TaskExecutor 사용");
        log.info("스프링부트 전용 스레드 풀 사용 톰캣 스레드 풀이랑은 다름!");
        log.info("효율이 좋음 -> 각 서버 사양에 따라 알맞게 설정하길");
        log.info("=======================");
    }




    @Async
    // @EventListener
    public void eventAsyncMethod() {
        log.info("@EventListener 어노테이션이 붙어있다고 가정했을 때");
        log.info("위 메소드는 동기적으로 사용하면 톰캣 스레드를 이어받아 사용하지만,");
        log.info("비동기 적으로 사용하면 비동기 스레드 풀에서 가져와서 사용한다!");
        log.info("====================");
    }


    @Scheduled(fixedRate = 10000)
    public void scheduledMethod() {
        log.info("ThreadPoolTaskExecutor 자동으로 사용");
        log.info("기본으로 단일 스레드 1개를 사욜하지만");
        log.info("같은 시간대에 여러 스케줄러가 돌아야 한다면");
        log.info("spring:\n" +
                "  task:\n" +
                "    scheduling:\n" +
                "      pool:\n" +
                "        size: 5\n"
                + "위 설정을 적용하자");
    }

    @Scheduled(fixedRate = 20000)
    public void scheduledMethod2() {
        log.info("ThreadPoolTaskExecutor 자동으로 사용");
        log.info("기본으로 단일 스레드 1개를 사욜하지만");
        log.info("같은 시간대에 여러 스케줄러가 돌아야 한다면");
        log.info("spring:\n" +
                "  task:\n" +
                "    scheduling:\n" +
                "      pool:\n" +
                "        size: 5\n"
                + "위 설정을 적용하자");
    }

    @Scheduled(fixedRate = 30000)
    public void scheduledMethod3() {
        log.info("ThreadPoolTaskExecutor 자동으로 사용");
        log.info("기본으로 단일 스레드 1개를 사욜하지만");
        log.info("같은 시간대에 여러 스케줄러가 돌아야 한다면");
        log.info("spring:\n" +
                "  task:\n" +
                "    scheduling:\n" +
                "      pool:\n" +
                "        size: 5\n"
                + "위 설정을 적용하자");
    }

}
