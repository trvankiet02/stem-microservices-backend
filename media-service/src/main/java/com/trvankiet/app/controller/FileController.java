package com.trvankiet.app.controller;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final JwtService jwtService;

        @GetMapping
        public String test() {
            return "Hello from FileController";
        }

        @PostMapping( "/post")
        public List<FileDto> uploadFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @RequestPart("mediaFiles") List<MultipartFile> mediaFiles) throws IOException {
            log.info("Received request to upload {} files", mediaFiles.size());
            String accessToken = authorizationHeader.substring(7);
            String userId = jwtService.extractUserId(accessToken);
            return fileService.uploadFiles(userId, mediaFiles);
        }
}
