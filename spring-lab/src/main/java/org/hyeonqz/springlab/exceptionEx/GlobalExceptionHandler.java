package org.hyeonqz.springlab.exceptionEx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MemberException.class)
	public ResponseEntity<String> handleMemberException(MemberException ex) {
		log.error("예외 발생: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ex.getMessage());
	}
}
