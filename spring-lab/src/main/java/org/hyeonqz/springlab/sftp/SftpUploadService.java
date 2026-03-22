package org.hyeonqz.springlab.sftp;

import com.jcraft.jsch.ChannelSftp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SftpUploadService {
    private final GenericObjectPool<ChannelSftp> sftpChannelPool;

    @Value("${sftp.upload-dir}")
    private String uploadDir;

    private static final int MAX_RETRY = 5;

    public void upload(String fileName, byte[] fileData) {
        int attempt = 0;
        long waitTime = 1000L; // 초기 대기 1초

        while (attempt < MAX_RETRY) {
            ChannelSftp channel = null;
            try {
                // 풀에서 세션 빌려오기
                channel = sftpChannelPool.borrowObject();
                log.info("[SFTP] 세션 획득 (풀 활성: {}/{})",
                        sftpChannelPool.getNumActive(),
                        sftpChannelPool.getMaxTotal());

                // 파일 업로드
                try (InputStream inputStream =
                             new ByteArrayInputStream(fileData)) {
                    channel.put(inputStream, uploadDir + "/" + fileName);
                }

                log.info("[SFTP] 업로드 성공: {}", fileName);
                return; // 성공 시 즉시 반환

            } catch (Exception e) {
                attempt++;
                log.warn("[SFTP] 업로드 실패 ({}/{}회): {}",
                        attempt, MAX_RETRY, e.getMessage());

                if (attempt >= MAX_RETRY) {
                    log.error("[SFTP] 최대 재시도 초과 → DLQ 처리: {}",
                            fileName);
                    throw new RuntimeException("SFTP 업로드 최종 실패: " + fileName, e);
                }

                // 지수 백오프 대기
                try {
                    log.info("[SFTP] {}ms 후 재시도...", waitTime);
                    Thread.sleep(waitTime);
                    waitTime *= 2; // 1s → 2s → 4s → 8s → 16s
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

            } finally {
                // 세션 반납 (항상 실행)
                if (channel != null) {
                    try {
                        sftpChannelPool.returnObject(channel);
                        log.info("[SFTP] 세션 반납 완료");
                    } catch (Exception e) {
                        log.error("[SFTP] 세션 반납 실패: {}",
                                e.getMessage());
                    }
                }
            }
        }
    }
}