package com.codingshuttle.linkedin.post_service.dto;

import com.codingshuttle.linkedin.post_service.enums.MediaType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data

public class PostRequestDto {
    private String content;
    private MultipartFile file;
}
