package com.trvankiet.app.controller;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.request.CommentPostRequest;
import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.request.UpdatePostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.CommentService;
import com.trvankiet.app.service.PostService;
import com.trvankiet.app.service.client.FileClientService;
import com.trvankiet.app.service.client.GroupClientService;
import com.trvankiet.app.util.FileUtil;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;
    private final FileClientService fileClientService;
    private final GroupClientService groupClientService;

    @GetMapping
    public ResponseEntity<GenericResponse> getPostInGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                          @RequestParam("groupId") String groupId,
                                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("PostController, getPostInGroup({})", groupId);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return postService.getPostInGroup(userId, groupId, page, size);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<GenericResponse> getPostById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("postId") String postId) {
        log.info("PostController, getPostById({})", postId);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return postService.getPostById(userId, postId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> createPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @ModelAttribute PostCreateRequest postCreateRequest) throws IOException {
        log.info("PostController, createPost({})", postCreateRequest);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        List<FileDto> fileDtos = FileUtil.isValidMultipartFiles(postCreateRequest.getMediaFiles()) ?
                fileClientService.uploadDocumentFiles(authorizationHeader, postCreateRequest.getMediaFiles()) : new ArrayList<>();
        return postService.createPost(userId, fileDtos, postCreateRequest);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updatePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @ModelAttribute UpdatePostRequest updatePostRequest) throws IOException {
        log.info("PostController, updatePost({})", updatePostRequest);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        List<FileDto> fileDtos = FileUtil.isValidMultipartFiles(updatePostRequest.getMediaFiles()) ?
                fileClientService.uploadDocumentFiles(authorizationHeader, updatePostRequest.getMediaFiles()) : new ArrayList<>();
        return postService.updatePost(userId, fileDtos, updatePostRequest);
    }

    @DeleteMapping(value = "/{postId}")
    public ResponseEntity<GenericResponse> deletePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @PathVariable String postId) {
        log.info("PostController, deletePost({})", postId);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return postService.deletePost(userId, postId);
    }

    @GetMapping("/search")
    public List<PostDto> searchPost(@RequestParam("query") Optional<String> query
    , @RequestParam("type") Optional<String> type) {
        log.info("PostController, searchPost");
        return postService.searchPost(query, type);
    }

    @GetMapping("/home-posts")
    public ResponseEntity<GenericResponse> getHomePost (@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("PostController, getHomePost");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        ResponseEntity<List<String>> groupIds = groupClientService.getGroupByUserId(authorizationHeader);
        return postService.getHomePost(groupIds.getBody(), page, size);
    }


}
