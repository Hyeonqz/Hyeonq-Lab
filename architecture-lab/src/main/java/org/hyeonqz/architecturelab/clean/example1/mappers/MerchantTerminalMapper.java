package org.hyeonqz.architecturelab.clean.example1.mappers;

import org.hyeonqz.architecturelab.clean.example1.domain.MerchantTerminal;
import org.hyeonqz.architecturelab.clean.example1.domain.MerchantTerminalDomain;


public class MerchantTerminalMapper {

    public static MerchantTerminalDomain toDomain(MerchantTerminal merchantTerminal) {
        return MerchantTerminalDomain.builder()
                .merchant(merchantTerminal.getMerchant())
                .uuid(merchantTerminal.getUuid())
                .shortUuid(merchantTerminal.getShortUuid())
                .name(merchantTerminal.getName())
                .createTime(merchantTerminal.getCreateTime())
                .createAt(merchantTerminal.getCreateAt())
                .createBy(merchantTerminal.getCreateBy())
                .updateTime(merchantTerminal.getUpdateTime())
                .updateAt(merchantTerminal.getUpdateAt())
                .updateBy(merchantTerminal.getUpdateBy())
                .deleteTime(merchantTerminal.getDeleteTime())
                .deleteAt(merchantTerminal.getDeleteAt())
                .deleteBy(merchantTerminal.getDeleteBy())
                .build();
    }
}
