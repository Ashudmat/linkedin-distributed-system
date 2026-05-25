package com.codingshuttle.linkedin.uploader_service.service;

import com.codingshuttle.linkedin.uploader_service.dto.UploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploaderService {
    UploadResponseDto upload(MultipartFile file) throws IOException;
}
