package com.trvankiet.app.service;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.request.UpdatePostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PostService {
    ResponseEntity<GenericResponse> createPost(String userId, List<FileDto> fileDtos, PostCreateRequest postCreateRequest);

    ResponseEntity<GenericResponse> updatePost(String userId, List<FileDto> fileDtos, UpdatePostRequest updatePostRequest);

    ResponseEntity<GenericResponse> deletePost(String userId, String postId);

    ResponseEntity<GenericResponse> getPostById(String userId, String postId);
}
