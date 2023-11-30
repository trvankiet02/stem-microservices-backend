package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.request.UpdatePostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.PostResponse;
import com.trvankiet.app.entity.Post;
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
        Post post = Post.builder()
                .id(UUID.randomUUID().toString())
                .groupId(postCreateRequest.getGroupId())
                .authorId(userId)
                .type(postTypeRepository.findByCode(postCreateRequest.getTypeCode())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy loại bài viết!")))
                .content(postCreateRequest.getContent())
                .refUrls(fileDtos.stream().map(FileDto::getRefUrl).toList())
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
        post.setType(postTypeRepository.findByCode(updatePostRequest.getTypeCode())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại bài viết!")));
        if (!fileDtos.isEmpty()) {
            post.setRefUrls(fileDtos.stream().map(FileDto::getRefUrl).toList());
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
        Reaction reaction = getReactionByUserIdInPost(userId, post);
        PostResponse postResponse = PostResponse.builder()
                .postDetailResponse(mapperService.mapToPostDetailResponse(post))
                .reactionDto(reaction == null ? null : mapperService.mapToReactionDto(reaction))
                .build();
        if (isUserInGroup(userId, post.getGroupId())) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .statusCode(HttpStatus.OK.value())
                            .message("Lấy bài viết thành công!")
                            .result(postResponse)
                            .build());
        }
        throw new ForbiddenException("Bạn không có quyền xem bài viết này!");
    }

    @Override
    public ResponseEntity<GenericResponse> getPostInGroup(String userId, String groupId, int page, int size) {
        if (isUserInGroup(userId, groupId)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Post> postPage = postRepository.findAllByGroupId(groupId, pageable);
            Map<String, Object> result = new HashMap<>();
            result.put("totalPages", postPage.getTotalPages());
            result.put("totalElements", postPage.getTotalElements());
            result.put("currentPage", postPage.getNumber());
            result.put("currentElements", postPage.getNumberOfElements());
            result.put("posts", postPage.getContent()
                    .stream()
                    .map(post -> {
                        if (post == null) {
                            return null;
                        }
                        Reaction reaction = getReactionByUserIdInPost(userId, post);
                        return PostResponse.builder()
                                .postDetailResponse(mapperService.mapToPostDetailResponse(post))
                                .reactionDto(reaction == null ? null : mapperService.mapToReactionDto(reaction))
                                .build();
                    }).toList());
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

    @Override
    public ResponseEntity<GenericResponse> getHomePost(List<String> groupIds, int page, int size) {
        log.info("PostServiceImpl, getHomePost");
        if (groupIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .statusCode(HttpStatus.OK.value())
                            .message("Lấy bài viết thành công!")
                            .result(null)
                            .build());
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAllByGroupIdIn(groupIds, pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("totalPages", postPage.getTotalPages());
        result.put("totalElements", postPage.getTotalElements());
        result.put("currentPage", postPage.getNumber());
        result.put("currentElements", postPage.getNumberOfElements());
        result.put("posts", postPage.getContent()
                .stream()
                .map(post -> {
                    if (post == null) {
                        return null;
                    }
                    Reaction reaction = getReactionByUserIdInPost(groupIds.get(0), post);
                    return PostResponse.builder()
                            .postDetailResponse(mapperService.mapToPostDetailResponse(post))
                            .reactionDto(reaction == null ? null : mapperService.mapToReactionDto(reaction))
                            .build();
                }).toList());
        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy bài viết thành công!")
                        .result(result)
                        .build());
    }

    public Boolean isUserInGroup(String userId, String groupId) {
        ResponseEntity<GenericResponse> responseEntity = groupClientService.validateUserInGroup(userId, groupId);
        return responseEntity.getStatusCode().equals(HttpStatus.OK);
    }

    public Reaction getReactionByUserIdInPost(String userId, Post post) {
        return post.getReactions().stream()
                .filter(reaction -> reaction.getAuthorId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
