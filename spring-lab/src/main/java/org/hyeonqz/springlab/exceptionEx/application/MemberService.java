package org.hyeonqz.springlab.exceptionEx.application;

import org.hyeonqz.springlab.exceptionEx.shared.MemberErrorCode;
import org.hyeonqz.springlab.exceptionEx.shared.MemberException;
import org.hyeonqz.springlab.exceptionEx.domain.Member;
import org.hyeonqz.springlab.exceptionEx.infrastructure.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final View error;

	public Member findMember(Long id) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new MemberException(MemberErrorCode.UNKNOWN_MEMBER_ID));

		return member;
	}
}
