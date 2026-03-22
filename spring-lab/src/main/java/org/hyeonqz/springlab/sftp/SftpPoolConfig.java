package org.hyeonqz.springlab.sftp;

import com.jcraft.jsch.ChannelSftp;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SftpPoolConfig {
    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port}")
    private int port;
    @Value("${sftp.username}")
    private String username;
    @Value("${sftp.password}")
    private String password;
    @Value("${sftp.upload-dir}")
    private String uploadDir;

    @Bean
    public GenericObjectPool<ChannelSftp> sftpChannelPool() {
        log.info("[SFTP] host: {}", host);
        log.info("[SFTP] port: {}", port);
        log.info("[SFTP] uploadDir: {}", uploadDir);

        if (host == null)
            throw new IllegalStateException("sftp.host가 null입니다. sftp.yml 로드 여부를 확인하세요.");

        GenericObjectPoolConfig<ChannelSftp> config = new GenericObjectPoolConfig<>();
        SftpPoolConfig.Pool pool = new SftpPoolConfig.Pool();

        config.setMaxTotal(pool.getMaxTotal());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        config.setMaxWait(Duration.ofMillis(pool.getMaxWaitMillis()));

        // Dead 세션 자동 검증 (핵심 설정)
        config.setTestOnBorrow(true);   // 꺼낼 때 검증
        config.setTestOnReturn(true);   // 반납 시 검증
        config.setTestWhileIdle(true);  // 유휴 상태 주기적 검증

        // JMX 모니터링 등록 X
        config.setJmxEnabled(false);

        return new GenericObjectPool<>(new SftpChannelFactory(host, port, username, password), config);
    }

    @Getter
    @Setter
    public static class Pool {
        private int maxTotal = 5;
        private int maxIdle = 3;
        private int minIdle = 1;
        private long maxWaitMillis = 3000;
    }
}
