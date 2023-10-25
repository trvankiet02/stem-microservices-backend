package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.entity.PostType;
import com.trvankiet.app.entity.Reaction;
import com.trvankiet.app.repository.PostRepository;
import com.trvankiet.app.repository.PostTypeRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostTypeRepository postTypeRepository;
    private final MapperService mapperService;
    @Override
    public ResponseEntity<GenericResponse> createPost(String userId, List<FileDto> fileDtos, PostCreateRequest postCreateRequest) {
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
                .filesId(fileDtos.stream().map(FileDto::getFileLink).toList())
                .build();
        PostDto postDto = mapperService.mapToPostDto(postRepository.save(post));
        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Đăng bài thành công!")
                        .result(postDto)
                        .build());
    }
}
