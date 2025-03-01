package org.hyeonqz.java_lab.thread_safe;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Concurrent {

	public void 동시성_컬렉션() {
		// Thread-safe Map
		Map<String, String> map = new ConcurrentHashMap<>();

		// Thread-safe Queue
		Queue<String> queue = new ConcurrentLinkedQueue<>();

		// Blocking Queue
		Queue<Integer> queue2 = new LinkedBlockingQueue<>(100);

		// copy-on-write 컬렉션 Read 작업 많을 때 효율적
		List<String> list = new CopyOnWriteArrayList<>();
	}
}
