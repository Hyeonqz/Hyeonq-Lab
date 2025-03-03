package org.hyeonqz.redislab.batch;

import java.net.UnknownHostException;

import org.hyeonqz.redislab.nosql.service.BatchLockService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class BatchScheduler {
	private final BatchLockService batchLockService;

	//@Scheduled(fixedRate = 20000, fixedDelay = 10000) // 20초 마다 실행, 실행 후 대기 10초
	@Scheduled(cron = "0 30 22 * * *")
	public void doRequest() throws UnknownHostException {
		batchLockService.runBatchWithLock();
	}

}
