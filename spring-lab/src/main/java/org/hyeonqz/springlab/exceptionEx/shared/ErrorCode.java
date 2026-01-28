package org.hyeonqz.springlab.exceptionEx.shared;

public interface ErrorCode {
	String getCode ();

	String getDescription ();

	default String getMessage () {
		return String.format("[%s] %s", getCode(), getDescription());
	}

}