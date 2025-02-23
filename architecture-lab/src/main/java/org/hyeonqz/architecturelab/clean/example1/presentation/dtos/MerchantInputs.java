package org.hyeonqz.architecturelab.clean.example1.presentation.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
public final class MerchantInputs {

    @Getter
    public static class RequestDto {

        @NotNull
        @Size(min = 1, max = 30)
        private String merchantName;

        @NotNull
        @Size(min = 1, max = 11)
        private String phoneNumber;
    }

}
