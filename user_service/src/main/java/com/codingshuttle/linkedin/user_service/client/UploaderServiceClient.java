package com.codingshuttle.linkedin.user_service.client;

import com.codingshuttle.linkedin.user_service.advice.ApiResponse;
import com.codingshuttle.linkedin.user_service.dto.UploadResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "uploader-service")
public interface UploaderServiceClient {

    @PostMapping(
            value = "/api/v1/uploads/media",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ResponseEntity<ApiResponse<UploadResponseDto>> uploadFile(
            @RequestPart("file") MultipartFile file
    );
}