package org.hyeonqz.springlab.exceptionEx;

public interface ErrorCode {
	String getCode ();

	String getDescription ();

	default String getMessage () {
		return String.format("[%s] %s", getCode(), getDescription());
	}

}