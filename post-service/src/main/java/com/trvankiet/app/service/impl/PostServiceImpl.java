package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.request.UpdatePostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.PostRepository;
import com.trvankiet.app.repository.PostTypeRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.PostService;
import com.trvankiet.app.service.client.GroupClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        Post post = Post.builder()
                .id(UUID.randomUUID().toString())
                .groupId(postCreateRequest.getGroupId())
                .authorId(userId)
                .type(postTypeRepository.findByCode(postCreateRequest.getTypeCode())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy loại bài viết!")))
                .content(postCreateRequest.getContent())
                .refUrls(fileDtos.stream().map(FileDto::getFileLink).toList())
                .reactions(new ArrayList<>())
                .comments(new ArrayList<>())
                .createdAt(new Date())
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
        Post post = postRepository.findById(updatePostRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        if (!post.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa bài viết này!");
        }
        post.setContent(updatePostRequest.getContent());
        post.setType(postTypeRepository.findByCode(updatePostRequest.getPostType())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại bài viết!")));
        if (!fileDtos.isEmpty()) {
            post.setRefUrls(fileDtos.stream().map(FileDto::getFileLink).toList());
        }
        post.setUpdatedAt(new Date());
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
        Post post = postRepository.findById(postId)
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
        Post post = postRepository.findById(postId)
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
//            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//            Page<Post> postPage = postRepository.findAll(pageable);
//            Map<String, Object> result = new HashMap<>();
//            result.put("totalPages", postPage.getTotalPages());
//            result.put("totalElements", postPage.getTotalElements());
//            result.put("currentPage", postPage.getNumber() + 1);
//            result.put("currentElements", postPage.getNumberOfElements());
//            result.put("posts", postPage.getContent().stream().map(mapperService::mapToPostDto).toList());
//            return ResponseEntity.status(HttpStatus.OK)
//                    .body(GenericResponse.builder()
//                            .success(true)
//                            .statusCode(HttpStatus.OK.value())
//                            .message("Lấy bài viết thành công!")
//                            .result(result)
//                            .build());
            List<Post> posts = postRepository.findAllByGroupId(groupId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .statusCode(HttpStatus.OK.value())
                            .message("Lấy bài viết thành công!")
                            .result(posts.stream().map(mapperService::mapToPostDto).toList())
                            .build());
        }
        throw new ForbiddenException("Bạn không có quyền xem bài viết trong nhóm này!");
    }

    @Override
    public List<PostDto> searchPost(Optional<String> query, Optional<String> type) {
        log.info("PostServiceImpl, searchPost");
        if (query.isPresent() && type.isPresent()) {
            return postRepository.searchPost(query.get()).stream()
                    .filter(post -> post.getType().getCode().equals(type.get()))
                    .map(mapperService::mapToPostDto)
                    .toList();
        } else {
            return query.map(s -> postRepository.searchPost(s).stream()
                            .map(mapperService::mapToPostDto)
                            .toList())
                    .orElseGet(() -> type.map(s -> postRepository.findAll().stream()
                                    .filter(post -> post.getType().getCode().equals(s))
                                    .map(mapperService::mapToPostDto)
                                    .toList())
                            .orElseGet(() -> postRepository.findAll().stream()
                                    .map(mapperService::mapToPostDto)
                                    .toList()));
        }
    }

    public Boolean isUserInGroup(String userId, String groupId) {
        ResponseEntity<GenericResponse> responseEntity = groupClientService.validateUserInGroup(userId, groupId);
        return responseEntity.getStatusCode().equals(HttpStatus.OK);
    }
}
