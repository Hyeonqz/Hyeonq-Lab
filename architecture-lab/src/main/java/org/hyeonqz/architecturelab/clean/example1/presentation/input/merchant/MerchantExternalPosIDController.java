package org.hyeonqz.architecturelab.clean.example1.presentation.input.merchant;

import org.hyeonqz.architecturelab.clean.example1.application.port.input.AddMerchantTerminalUseCase;
import org.hyeonqz.architecturelab.clean.example1.presentation.dtos.MerchantInputs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/apis")
@RestController
public class MerchantExternalPosIDController {

    private final AddMerchantTerminalUseCase addMerchantTerminalUseCase;

    @PostMapping("/issue")
    public ResponseEntity<?> IssuePosId(@RequestBody @Valid MerchantInputs.RequestDto posIdDto) {


        return ResponseEntity.ok("hi");
    }


}
