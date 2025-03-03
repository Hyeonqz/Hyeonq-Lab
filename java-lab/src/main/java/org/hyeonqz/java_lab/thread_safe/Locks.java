package org.hyeonqz.java_lab.thread_safe;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Locks {

	public void lock() {
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			// 임계 구역
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void semaphore() throws InterruptedException {
		Semaphore semaphore = new Semaphore(5); // 최대 5개 스레드 동시 접근
		semaphore.acquire();

		try {
			// 리소스 접근
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			semaphore.release();
		}
	}


}
