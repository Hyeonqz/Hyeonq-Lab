package org.hyeonqz.springlab.sftp;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SftpUploadServiceTest {

    @Autowired
    private SftpUploadService sftpUploadService;

    @Autowired
    private GenericObjectPool<ChannelSftp> sftpChannelPool;

    @Test
    @DisplayName("단건 업로드 성공")
    void uploadSingleFile() throws IOException, URISyntaxException {
        byte[] content = Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource("test.txt").toURI())
        );
        assertDoesNotThrow(() ->
                sftpUploadService.upload("test.txt", content));
    }

    @Test
    @DisplayName("동시 10건 업로드 - 세션 풀 동작 검증")
    void uploadConcurrent() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    sftpUploadService.upload(
                            "test_" + idx + ".txt",
                            ("파일 내용 " + idx).getBytes()
                    );
                    System.out.println("업로드 완료=" + idx);
                } catch (Exception e) {
                    System.out.println("업로드 실패=" + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        System.out.println("풀 상태 - 활성: " + sftpChannelPool.getNumActive() + ", 유휴: " + sftpChannelPool.getNumIdle());
    }
}