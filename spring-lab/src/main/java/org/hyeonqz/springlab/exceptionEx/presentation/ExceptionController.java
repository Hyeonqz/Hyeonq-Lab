package org.hyeonqz.springlab.exceptionEx.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExceptionController {

    @PostMapping("/apis/v1/exception-1")
    public ResponseEntity<?> exception1() {

        return ResponseEntity.ok("hi");
    }
}
