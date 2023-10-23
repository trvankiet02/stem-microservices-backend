package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;

    @GetMapping
    public String getPosts() {
        return "Hello from PostController";
    }

    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody PostCreateRequest postCreateRequest) {
        log.info("PostController, createPost({})", postCreateRequest);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return postService.createPost(userId, postCreateRequest);
    }
}
