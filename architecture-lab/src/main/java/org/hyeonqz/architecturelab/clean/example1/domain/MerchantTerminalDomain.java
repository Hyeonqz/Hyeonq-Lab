package org.hyeonqz.architecturelab.clean.example1.domain;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class MerchantTerminalDomain {

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

    // 도메인 로직 작성
}
