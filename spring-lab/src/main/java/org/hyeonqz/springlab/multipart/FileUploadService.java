package org.hyeonqz.springlab.multipart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final static int PERMISSION = 0_770;
    private static final String UNIX_SEPARATOR = "/";

    @Async(value = "fileTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void uploadFileList(List<MultipartFile> multipartFileList) throws Exception {

    }
}
