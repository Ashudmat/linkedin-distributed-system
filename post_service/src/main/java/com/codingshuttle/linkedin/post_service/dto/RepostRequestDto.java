package com.codingshuttle.linkedin.post_service.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RepostRequestDto {
    private String content;
    private MultipartFile file;
}
