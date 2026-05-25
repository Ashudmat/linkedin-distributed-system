package com.codingshuttle.linkedin.uploader_service.service;

import com.cloudinary.Cloudinary;
import com.codingshuttle.linkedin.uploader_service.dto.UploadResponseDto;
import com.codingshuttle.linkedin.uploader_service.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryUploaderService implements UploaderService {

    private final Cloudinary cloudinary;

    @Override
    public UploadResponseDto upload(MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()){
            throw new BadRequestException("File cannot be empty");
        }
        String contentType = file.getContentType();
        String resourceType = contentType != null && contentType.startsWith("image/") ? "image" : "raw";
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of("resource_type", resourceType));
        return new UploadResponseDto(uploadResult.get("secure_url").toString());
    }
}
