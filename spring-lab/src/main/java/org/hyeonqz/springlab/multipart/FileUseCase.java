package org.hyeonqz.springlab.multipart;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class FileUseCase {

    public void saveFileData(Map<String, String> requestDto, FileData fileData) {

    }

    public boolean saveFileData(Map<String, String> requestDto, List<MultipartFile> individualFiles, List<MultipartFile> corporateFiles) {


        return true;
    }
}
