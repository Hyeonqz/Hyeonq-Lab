package org.hyeonqz.springlab.thread_example.presentation;

import lombok.RequiredArgsConstructor;
import org.hyeonqz.springlab.thread_example.AsyncExampleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AsyncExampleController {
    private final AsyncExampleService asyncExampleService;

    @GetMapping("/api/v1/async")
    public ResponseEntity<?> async() {
        asyncExampleService.integrationAsync();

        return ResponseEntity.ok("SUCCESS");
    }
}
