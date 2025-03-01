package org.hyeonqz.redislab.nosql.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;

@RedisHash("pos") // 키 이름 pos:<posId>
public class Pos {
	// set 은 키 목록 관리용
	// Hset 은 실제 데이터 저장용

	@Id
	private String posId; // 각 객체의 고유 키
	private String posName;
	private String merchantName;

	@TimeToLive
	private Long expiration;

	@Builder
	public Pos (String posId, String posName, String merchantName, Long expiration) {
		this.posId = posId;
		this.posName = posName;
		this.merchantName = merchantName;
		this.expiration = expiration;
	}

	public String getPosId () {
		return posId;
	}

	public String getPosName () {
		return posName;
	}

	public String getMerchantName () {
		return merchantName;
	}

	public Long getExpiration () {
		return expiration;
	}

}
