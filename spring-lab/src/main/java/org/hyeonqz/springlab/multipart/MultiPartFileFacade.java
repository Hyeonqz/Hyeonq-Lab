package org.hyeonqz.springlab.multipart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MultiPartFileFacade {
    private final FileUploadService fileUploadService;
    private final FileUseCase fileUseCase;

    @Transactional
    public void processFileUpload(List<MultipartFile> individualFiles, List<MultipartFile> corporateFiles, Map<String, String> requestDto) {

        //1. dto validation 체크 후 dto save 및 file 관련 데이터 db save
        if(fileUseCase.saveFileData(requestDto, individualFiles, corporateFiles)) {
            //2. individualFiles 파일서버 upload
            //fileUploadService.uploadFileList(individualFiles);

            //3. corporateFiles 가 존재할 경우 파일서버 upload
/*            if(corporateFiles.size() != 0)
                fileUploadService.uploadFileList(corporateFiles);*/
        }
    }

}
