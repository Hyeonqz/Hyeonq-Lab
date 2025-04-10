package org.hyeonqz.java_lab.sealed;

public abstract sealed class Pet {
	private final String name;

	protected Pet (String name) {
		this.name = name;
	}

	public String getName () {
		return name;
	}

	public static final class Cat extends Pet {
		public Cat (String name) {
			super(name);
		}

		void meow() {
			System.out.println(getName() + " meows");
		}

	}

	public static final class Dog extends Pet {
		public Dog (String name) {
			super(name);
		}

		void bark() {
			System.out.println(getName() + " meong mung");
		}

	}


}
