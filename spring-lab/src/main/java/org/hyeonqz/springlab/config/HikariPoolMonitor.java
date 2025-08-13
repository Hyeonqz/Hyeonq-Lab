package org.hyeonqz.springlab.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Component
public class HikariPoolMonitor {
    private final DataSource dataSource;

    @EventListener
    public void handleContextRefreshedEvent(final ContextRefreshedEvent event) {
        monitorHikariCp();
    }

    @Scheduled(fixedRate = 60000) // 60초 주기
    public void monitorHikariCp() {
        if(dataSource instanceof HikariDataSource hikariDataSource) {
            HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

            log.info("===HikariCP Pool Status===");
            log.info("Active Connections: {}", poolMXBean.getActiveConnections());
            log.info("Idle Connections: {}", poolMXBean.getIdleConnections());
            log.info("Total Connections: {}", poolMXBean.getTotalConnections());
            log.info("Threads awaiting connection : {}", poolMXBean.getThreadsAwaitingConnection());
            log.info("========================================");

        } else {
            log.warn("DataSource is not HikariDataSource : {}", dataSource.getClass().getName());
        }
    }
}