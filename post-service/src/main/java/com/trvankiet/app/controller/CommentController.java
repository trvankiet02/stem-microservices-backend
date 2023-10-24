package com.trvankiet.app.controller;

import com.trvankiet.app.dto.response.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    @GetMapping
    public String home() {
        return "Hello from CommentController";
    }

    @PostMapping("/comment-post")
    public ResponseEntity<GenericResponse> commentPost() {
        log.info("CommentController, commentPost");
        return null;
    }
}
