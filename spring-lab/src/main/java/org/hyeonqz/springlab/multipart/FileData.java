package org.hyeonqz.springlab.multipart;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record FileData(
        String originalFilename,
        String contentType,
        long size,
        byte[] content
) {

    /**
     * MultipartFile 을 DTO 로 만들어서 비동기 처리하기 위한 메소드
     *
     * @param files MultiPart 파일 데이터
     * @return List<FileData>
     * @throws IOException getBytes() 시 IOException 발생 가능
     */
    public static List<FileData> from(List<MultipartFile> files) throws IOException {
        List<FileData> fileDataList = new ArrayList<>();

        for (MultipartFile file : files) {
            FileData fileData = new FileData(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes()
            );

            fileDataList.add(fileData);
        }

        return fileDataList;
    }
}