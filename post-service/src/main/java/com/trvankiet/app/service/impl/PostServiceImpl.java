package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.request.UpdatePostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.entity.PostType;
import com.trvankiet.app.entity.Reaction;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.PostRepository;
import com.trvankiet.app.repository.PostTypeRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.PostService;
import com.trvankiet.app.service.client.GroupClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostTypeRepository postTypeRepository;
    private final MapperService mapperService;
    private final GroupClientService groupClientService;
    @Override
    public ResponseEntity<GenericResponse> createPost(String userId, List<FileDto> fileDtos, PostCreateRequest postCreateRequest) {
        log.info("PostServiceImpl, createPost({})", postCreateRequest);
        if (!isUserInGroup(userId, postCreateRequest.getGroupId()))
            throw new ForbiddenException("Bạn không có quyền đăng bài viết vào nhóm này!");
        Date date = new Date();
        Post post = Post.builder()
                .postId(UUID.randomUUID().toString())
                .groupId(postCreateRequest.getGroupId())
                .authorId(userId)
                .accessibility("PUBLIC")
                .postType(postTypeRepository.findByPostTypeName(postCreateRequest.getPostType())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy loại bài viết!")))
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

    @Override
    public ResponseEntity<GenericResponse> updatePost(String userId, List<FileDto> fileDtos, UpdatePostRequest updatePostRequest) {
        log.info("PostServiceImpl, updatePost({})", updatePostRequest);
        Post post = postRepository.findByPostId(updatePostRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        Date now = new Date();
        if (!isUserInGroup(userId, post.getGroupId())) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa bài viết này!");
        }
        post.setContent(updatePostRequest.getContent());
        post.setPostType(postTypeRepository.findByPostTypeName(updatePostRequest.getPostType())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại bài viết!")));
        post.setFilesId(fileDtos.stream().map(FileDto::getFileLink).toList());
        post.setUpdatedAt(now);
        PostDto postDto = mapperService.mapToPostDto(postRepository.save(post));
        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Cập nhật bài viết thành công!")
                        .result(postDto)
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deletePost(String userId, String postId) {
        log.info("PostServiceImpl, deletePost({})", postId);
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết!"));
        if (!post.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền xóa bài viết này!");
        }
        postRepository.delete(post);
        return ResponseEntity.ok()
                .body(GenericResponse.builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Xóa bài viết thành công!")
                        .result(null)
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getPostById(String userId, String postId) {
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết!"));
        if (isUserInGroup(userId, post.getGroupId())) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .statusCode(HttpStatus.OK.value())
                            .message("Lấy bài viết thành công!")
                            .result(mapperService.mapToPostDto(post))
                            .build());
        }
        throw new ForbiddenException("Bạn không có quyền xem bài viết này!");
    }

    @Override
    public ResponseEntity<GenericResponse> getPostInGroup(String userId, String groupId, int page, int size) {
        if (isUserInGroup(userId, groupId)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Post> postPage = postRepository.findAll(pageable);
            Map<String, Object> result = new HashMap<>();
            result.put("totalPages", postPage.getTotalPages());
            result.put("totalElements", postPage.getTotalElements());
            result.put("currentPage", postPage.getNumber() + 1);
            result.put("currentElements", postPage.getNumberOfElements());
            result.put("posts", postPage.getContent().stream().map(mapperService::mapToPostDto).toList());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .statusCode(HttpStatus.OK.value())
                            .message("Lấy bài viết thành công!")
                            .result(result)
                            .build());
        }
        throw new ForbiddenException("Bạn không có quyền xem bài viết trong nhóm này!");
    }

    public Boolean isUserInGroup(String userId, String groupId) {
        ResponseEntity<GenericResponse> responseEntity = groupClientService.validateUserInGroup(userId, groupId);
        return responseEntity.getStatusCode().equals(HttpStatus.OK);
    }
}
