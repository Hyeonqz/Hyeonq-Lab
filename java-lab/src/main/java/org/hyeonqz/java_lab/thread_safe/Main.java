package org.hyeonqz.java_lab.thread_safe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
	int count = 0;

	public int increment () {
		return count++;
	}

	public int decrease () {
		return count--;
	}

	public synchronized int increment2() {
		return count++;
	}

	public synchronized int decrease2() {
		return count--;
	}

	public static void main (String[] args) {
		Main main = new Main();
		int increment = main.increment();
		System.out.println(increment);

		int decrease = main.decrease();
		System.out.println(decrease);

		ImmutablePerson immutable = new ImmutablePerson("Jin");
		String name = immutable.getName();
		System.out.println(name);

		// non thread-safe
		List<String> list = new ArrayList<>();
		// thread-safe
		List<String> syncList = Collections.synchronizedList(list);

		Set<String> set  = new HashSet<>();
		Set<String> syncSet = Collections.synchronizedSet(set);

		Map<String, String> syncMap = Collections.synchronizedMap(new HashMap<String, String>());

	}

}
