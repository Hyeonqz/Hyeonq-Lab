package org.hyeonqz.redislab.entity;


import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.Id;

@RedisHash("pos")
public class Pos {

	@Id
	private String posId;

	@Indexed
	private String transactionNo;


}
