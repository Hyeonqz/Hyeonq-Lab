package org.hyeonqz.redislab.nosql.service;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hyeonqz.redislab.batch.BatchService;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BatchLockService {
	private final Environment environment;

	private final RedisTemplate<String, String> redisTemplate;
	private static final String LOCK_KEY = "batch:lock"; // 락 키
	private static final String LOCK_INFO_KEY = "batch:lock:info"; // 락 정보용 키
	private static final String LOCK_VALUE = "locked"; // 락 값
	private static final long LOCK_TIMEOUT = 60; // 락 만료 시간(초)

	private final BatchService batchService;

	/** 락 획득 */
	public boolean acquireLock() {
		// SET batch:lock "locked" NX EX 60
		return Boolean.TRUE.equals(redisTemplate.opsForValue()
			.setIfAbsent(LOCK_KEY, LOCK_VALUE, LOCK_TIMEOUT, TimeUnit.SECONDS)
		);
	}

	public void releaseLock() {
		// DEL batch:lock
		// DEL batch:lock:info
		redisTemplate.delete(LOCK_KEY);
		redisTemplate.delete(LOCK_INFO_KEY);
	}

	// 배치 실행 로직 (락을 활용)
	public void runBatchWithLock() throws UnknownHostException {
		String ip = InetAddress.getLocalHost().getHostAddress();
		String port = environment.getProperty("server.port", "unknown");

		if (acquireLock()) {
			try {
				// Hash 설정
				redisTemplate.opsForHash().putAll(LOCK_INFO_KEY, Map.of(
					"ip", ip,
					"port", port
				));
				redisTemplate.expire(LOCK_INFO_KEY, LOCK_TIMEOUT, TimeUnit.SECONDS);

				log.info("배치 실행 시작 {}: ", Thread.currentThread().getName());
				log.info("Lock 획득 서버 {}:{}", ip,port);

				batchService.execute();

				Thread.sleep(1000);
				log.info("배치 실행 완료");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				releaseLock();
			}
		} else {
			// HGETALL batch:lock:info
			Map<Object, Object> lockInfo = redisTemplate.opsForHash().entries(LOCK_INFO_KEY);
			String lockedIp = lockInfo.getOrDefault("ip","unknown").toString();
			String lockedPort = lockInfo.getOrDefault("port","unknown").toString();

			log.info("{}:{} 서버가 이미 배치를 실행 중 입니다.\n Lock 소유자 : {}:{}",ip, port, lockedIp, lockedPort);
		}
	}

}
