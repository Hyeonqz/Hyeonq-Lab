package org.hyeonqz.springlab.exceptionEx;

import org.springframework.validation.Errors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberException extends RuntimeException{

	private ErrorCode errorCode;
	private Errors errors;
	private Throwable errorCause;

	public MemberException(ErrorCode errorCode) {
		super(errorCode.getMessage());

		this.errorCode = errorCode;
	}

	public MemberException(ErrorCode errorCode, Errors errors) {
		super(errorCode.getMessage());

		this.errorCode = errorCode;
		this.errors = errors;
	}

	public MemberException(ErrorCode errorCode, Throwable errorCause) {
		super(errorCode.getMessage(), errorCause);

		this.errorCode = errorCode;
		this.errorCause = errorCause;
	}

	public MemberException(ErrorCode errorCode, Errors errors, Throwable errorCause) {
		super(errorCode.getMessage(), errorCause);

		this.errorCode = errorCode;
		this.errors = errors;
		this.errorCause = errorCause;
	}

	public MemberException(ErrorCode errorCode, String message) {
		super(String.format("%s (Value: %s)", errorCode.getMessage(), message));
		this.errorCode = errorCode;

	}

}
