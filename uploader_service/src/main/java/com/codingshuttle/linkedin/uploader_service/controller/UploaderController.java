package com.codingshuttle.linkedin.uploader_service.controller;


import com.codingshuttle.linkedin.uploader_service.service.UploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
public class UploaderController {

    private final UploaderService uploaderService;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file) throws IOException {
        String url = uploaderService.upload(file);
        return ResponseEntity.ok(url);
    }
}
