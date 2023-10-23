package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.entity.PostType;
import com.trvankiet.app.entity.Reaction;
import com.trvankiet.app.repository.PostRepository;
import com.trvankiet.app.repository.PostTypeRepository;
import com.trvankiet.app.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostTypeRepository postTypeRepository;
    @Override
    public ResponseEntity<GenericResponse> createPost(String userId, PostCreateRequest postCreateRequest) {
        Date date = new Date();
        Post post = Post.builder()
                .postId(UUID.randomUUID().toString())
                .groupId(postCreateRequest.getGroupId())
                .authorId(userId)
                .accessibility("PUBLIC")
                .postType(postTypeRepository.findByPostTypeName(postCreateRequest.getPostType()).orElseThrow(() -> new RuntimeException("Post type not found")))
                .content(postCreateRequest.getContent())
                .reactions(new ArrayList<>())
                .comments(new ArrayList<>())
                .createdAt(date)
                .build();



        return null;
    }
}
