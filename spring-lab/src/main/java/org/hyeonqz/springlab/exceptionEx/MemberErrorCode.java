package org.hyeonqz.springlab.exceptionEx;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
	UNKNOWN_MEMBER_ID(INTERNAL_SERVER_ERROR,"ID 에 해당하는 회원이 존재하지 않습니다."),
	BAD_CREDENTIALS(UNAUTHORIZED, "계정 정보가 잘못되었습니다."),
	;

	private final HttpStatus httpStatus;
	private final String description;

	@Override
	public String getCode () {
		return this.name();
	}
}
