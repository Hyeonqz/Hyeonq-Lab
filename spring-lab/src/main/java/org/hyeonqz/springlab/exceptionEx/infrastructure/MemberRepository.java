package org.hyeonqz.springlab.exceptionEx.infrastructure;

import org.hyeonqz.springlab.exceptionEx.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
