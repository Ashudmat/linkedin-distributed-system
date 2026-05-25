package com.codingshuttle.linkedin.uploader_service.service;

import com.codingshuttle.linkedin.uploader_service.dto.UploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

public class GoogleCloudStorageUploaderService implements UploaderService {
    @Override
    public UploadResponseDto upload(MultipartFile file) {
        return new UploadResponseDto(null);
        //TODO:
    }
}
