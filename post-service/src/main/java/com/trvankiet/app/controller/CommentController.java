package com.trvankiet.app.controller;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.request.CommentPostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.CommentService;
import com.trvankiet.app.service.client.FileClientService;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> createComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                         @ModelAttribute CommentPostRequest commentPostRequest) {
        log.info("PostController, createComment({})", commentPostRequest);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        List<FileDto> fileDtos = commentPostRequest.getMediaFiles().isEmpty() ?
                new ArrayList<>() : fileClientService.uploadCommentFiles(authorizationHeader, commentPostRequest.getMediaFiles());
        return commentService.createComment(userId, fileDtos, commentPostRequest);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<GenericResponse> updateComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                         @PathVariable("commentId") String commentId,
                                                         @ModelAttribute CommentPostRequest commentPostRequest) {
        log.info("PostController, updateComment({})", commentPostRequest);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        List<FileDto> fileDtos = commentPostRequest.getMediaFiles().isEmpty() ?
                new ArrayList<>() : fileClientService.uploadCommentFiles(authorizationHeader, commentPostRequest.getMediaFiles());
        return commentService.updateComment(userId, commentId, fileDtos, commentPostRequest);
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
