package org.hyeonqz.java_lab.thread_safe;

public final class ImmutablePerson {
	private final String name;

	public ImmutablePerson (String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
