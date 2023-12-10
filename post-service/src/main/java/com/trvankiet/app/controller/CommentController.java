package com.trvankiet.app.controller;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.request.CommentPostRequest;
import com.trvankiet.app.dto.request.UpdateCommentRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.CommentService;
import com.trvankiet.app.service.client.FileClientService;
import com.trvankiet.app.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final JwtService jwtService;
    private final CommentService commentService;
    private final FileClientService fileClientService;

    @GetMapping
    public ResponseEntity<GenericResponse> getComments(@RequestParam("postId") String postId,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("PostController, getComments({})", postId);
        return commentService.getComments(postId, page, size);
    }

    @PostMapping(value = "/commentPost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> createComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                         @ModelAttribute CommentPostRequest commentPostRequest) {
        log.info("PostController, createComment()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return commentService.createComment(userId, authorizationHeader, commentPostRequest);
    }

    @PostMapping(value = "/repComment/{commentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> createRepComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                            @ModelAttribute UpdateCommentRequest updateCommentRequest,
                                                            @PathVariable("commentId") String commentId) {
        log.info("PostController, createRepComment()");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return commentService.createRepComment(userId, commentId, authorizationHeader, updateCommentRequest);
    }

    @PutMapping(value = "/{commentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                         @PathVariable("commentId") String commentId,
                                                         @ModelAttribute UpdateCommentRequest updateCommentRequest) {
        log.info("PostController, updateComment({})", updateCommentRequest);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return commentService.updateComment(userId, commentId, authorizationHeader, updateCommentRequest);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<GenericResponse> deleteComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                         @PathVariable("commentId") String commentId) {
        log.info("PostController, deleteComment({})", commentId);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return commentService.deleteComment(userId, commentId);
    }


}
