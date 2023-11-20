package com.trvankiet.app.service;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.request.UpdatePostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface PostService {
    ResponseEntity<GenericResponse> createPost(String userId, List<FileDto> fileDtos, PostCreateRequest postCreateRequest);

    ResponseEntity<GenericResponse> updatePost(String userId, List<FileDto> fileDtos, UpdatePostRequest updatePostRequest);

    ResponseEntity<GenericResponse> deletePost(String userId, String postId);

    ResponseEntity<GenericResponse> getPostById(String userId, String postId);

    ResponseEntity<GenericResponse> getPostInGroup(String userId, String groupId, int page, int size);

    List<PostDto> searchPost(Optional<String> query, Optional<String> type);
}
