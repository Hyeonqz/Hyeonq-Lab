package io.github.springreactivelab.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class SchedulerConfig {

    @Bean
    public Scheduler jpaScheduler() {
        return Schedulers.newBoundedElastic(
                Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,  // thread cap
                Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
                "jpa-worker"
        );
    }
}
