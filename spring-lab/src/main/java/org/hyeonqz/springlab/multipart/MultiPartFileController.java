package org.hyeonqz.springlab.multipart;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MultiPartFileController {
    private final MultiPartFileFacade multiPartFileFacade;

    @PostMapping("/api/upload")
    public ResponseEntity<?> upload(
            @RequestPart(value = "individualFiles", required = true) List<MultipartFile> individualFiles,
            @RequestPart(value = "corporateFiles", required = false) List<MultipartFile> corporateFiles,
            Map<String, String> requestDto
            ) {

        multiPartFileFacade.processFileUpload(individualFiles, corporateFiles, requestDto);


        return ResponseEntity.ok(
                Map.of("result","success")
        );
    }
}
