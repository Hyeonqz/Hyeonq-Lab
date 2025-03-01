package org.hyeonqz.java_lab.thread_safe;

public class Thread_Local {

	public void setThreadLocal () {
		ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

		threadLocal.set(120);

		Integer i = threadLocal.get();
		System.out.println(i);

		threadLocal.remove();
	}

	public static void main (String[] args) {
		Thread_Local threadLocal = new Thread_Local();
		threadLocal.setThreadLocal();
	}

}
