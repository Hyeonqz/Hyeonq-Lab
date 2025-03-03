package org.hyeonqz.redislab.nosql.service;

import static org.junit.jupiter.api.Assertions.*;

import java.net.UnknownHostException;

import org.hyeonqz.redislab.RedisLabApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = RedisLabApplication.class)
class BatchLockServiceTest {

	@Autowired
	private BatchLockService batchLockService;

	@Test
	@DisplayName("Lock 을 얻는다")
	void acquire_Lock_Test() {
	    // given & when & then
		batchLockService.acquireLock();
	}

	@Test
	@DisplayName("Lock 을 해제한다")
	void release_Lock_Test() {
	    // given & when & then
		batchLockService.releaseLock();
	}

	@Test
	@DisplayName("Lock 을 사용하여 배치를 실행한다.")
	void run_Batch_With_Lock_Test() throws UnknownHostException {
	    // given & when & then
		batchLockService.runBatchWithLock();
	}

}