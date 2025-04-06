package org.hyeonqz.redislab.nosql.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@RedisHash("terminal") // 키 이름 terminal:@Id -> @Id 를 기준으로 key 이름을 만든다.
public class PosTerminal {
	/*
	* @RedisHash 사용시 자동으로 생성하는 것
	* 1. terminal:<@Id> -> 실제 데이터 저장 (Hash 구조)
	* 2. terminal -> 전체 객체 목록 관리용 Set (모든 키 모음)
	* 3. terminal:<@Id>:idx -> @Indexed 가 있을시 인덱싱 정보 저장용 set
	* 4. terminal:id:<@Id> -> @Indexed 필드용 인덱스 Set
	* */

	@Indexed @Id
	private String posId;

	private Long id;

	private String posName;


	@Builder
	public PosTerminal (String posId, String posName, Long id) {
		this.posId = posId;
		this.posName = posName;
		this.id = id;
	}

}
