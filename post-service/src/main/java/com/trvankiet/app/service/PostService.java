package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface PostService {
    ResponseEntity<GenericResponse> createPost(String userId, PostCreateRequest postCreateRequest);
}
