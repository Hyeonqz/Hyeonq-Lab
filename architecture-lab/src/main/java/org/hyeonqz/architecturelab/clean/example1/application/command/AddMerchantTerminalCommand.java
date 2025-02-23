package org.hyeonqz.architecturelab.clean.example1.application.command;

import lombok.Getter;


@Getter
public record AddMerchantTerminalCommand(
        String merchantName,
        String posName) {

    public AddMerchantTerminalCommand {
        // Util 클래스를 만들어서 생성자를 통해 검증을 한다.
        //CommandUtil.throwIfNull(merchantName, "merchantName is null or blank.");
        //CommandUtil.throwIfNull(posName, "posName is null or blank.");
    }

}
