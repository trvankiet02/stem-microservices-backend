package com.trvankiet.app.controller;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.PostService;
import com.trvankiet.app.service.client.FileClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;
    private final FileClientService fileClientService;

    @GetMapping
    public String getPosts() {
        return "Hello from PostController";
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<GenericResponse> createPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @ModelAttribute PostCreateRequest postCreateRequest) throws IOException {
        log.info("PostController, createPost({})", postCreateRequest);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        List<FileDto> fileDtos = fileClientService.uploadFiles(authorizationHeader, postCreateRequest.getMediaFiles());
        return postService.createPost(userId, fileDtos, postCreateRequest);
    }

}
