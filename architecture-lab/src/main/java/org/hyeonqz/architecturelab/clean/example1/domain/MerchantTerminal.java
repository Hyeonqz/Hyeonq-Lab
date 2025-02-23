package org.hyeonqz.architecturelab.clean.example1.domain;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MerchantTerminal {

	// 실제 Entity 클래스임 편의를 위해서 domain 패키지에 넣어둠
	// 실무에서 위구조는 좋지 않음.

	private Long id;

	private MerchantDomain merchant;

	private UUID uuid;

	private String shortUuid;

	private String name;

	private Instant createTime;

	private String createAt;

	private String createBy;

	private Instant updateTime;

	private String updateAt;

	private String updateBy;

	private Instant deleteTime;

	private String deleteAt;

	private String deleteBy;
}
