package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.PostTypeEnum;
import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.SimpleGroupDto;
import com.trvankiet.app.dto.SimpleUserDto;
import com.trvankiet.app.dto.request.PostCreateRequest;
import com.trvankiet.app.dto.request.UpdatePostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.PostResponse;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.entity.Reaction;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.PostRepository;
import com.trvankiet.app.repository.ReactionRepository;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.PostService;
import com.trvankiet.app.service.client.FileClientService;
import com.trvankiet.app.service.client.GroupClientService;
import com.trvankiet.app.service.client.UserClientService;
import com.trvankiet.app.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MapperService mapperService;
    private final GroupClientService groupClientService;
    private final FileClientService fileClientService;
    private final MongoTemplate mongoTemplate;
    private final ReactionRepository reactionRepository;

    @Override
    public ResponseEntity<GenericResponse> createPost(String userId, SimpleGroupDto groupDto, List<FileDto> fileDtos,
                                                      PostCreateRequest postCreateRequest) {
        log.info("PostServiceImpl, createPost({})", postCreateRequest);
        try {
            Post post = Post.builder()
                    .id(UUID.randomUUID().toString())
                    .groupId(postCreateRequest.getGroupId())
                    .authorId(userId)
                    .type(PostTypeEnum.valueOf(postCreateRequest.getTypeName()))
                    .content(postCreateRequest.getContent())
                    .isPublic(groupDto.getIsPublic())
                    .refUrls(fileDtos.stream().map(FileDto::getRefUrl).toList())
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
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Không tìm thấy loại bài viết!");
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<GenericResponse> updatePost(String userId, String authorizationHeader, UpdatePostRequest updatePostRequest) {
        log.info("PostServiceImpl, updatePost({})", updatePostRequest);
        try {
            Post post = postRepository.findById(updatePostRequest.getPostId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
            if (!post.getAuthorId().equals(userId)) {
                throw new ForbiddenException("Bạn không có quyền chỉnh sửa bài viết này!");
            }
            List<FileDto> fileDtos = FileUtil.isValidMultipartFiles(updatePostRequest.getMediaFiles()) ?
                    fileClientService.uploadDocumentFiles(authorizationHeader,
                            updatePostRequest.getMediaFiles(), post.getGroupId()) : new ArrayList<>();

            post.setContent(updatePostRequest.getContent());
            post.setType(PostTypeEnum.valueOf(updatePostRequest.getTypeName()));
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
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Không tìm thấy loại bài viết!");
        }
        catch (Exception e) {
            throw new BadRequestException("Cập nhật bài viết thất bại!");
        }
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
                .postDto(mapperService.mapToPostDto(post))
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
                                .postDto(mapperService.mapToPostDto(post))
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
        Query mongoQuery = new Query();
        String queryValue = query.orElse("");
        Criteria queryCriteria = new Criteria().orOperator(
                Criteria.where("content").regex(queryValue),
                Criteria.where("authorFirstName").regex(queryValue),
                Criteria.where("authorLastName").regex(queryValue)
        );

        mongoQuery.addCriteria(queryCriteria);
        mongoQuery.addCriteria(Criteria.where("isPublic").is(true));
        mongoQuery.with(Sort.by("createdAt").descending());
        type.ifPresent(s -> mongoQuery.addCriteria(Criteria.where("type").is(s)));
        return mongoTemplate.find(mongoQuery, Post.class).stream()
                .map(mapperService::mapToPostDto)
                .toList();
    }

    @Override
    public ResponseEntity<GenericResponse> getHomePost(String userId, List<String> groupIds, int page, int size) {
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
                    Reaction reaction = getReactionByUserIdInPost(userId, post);
                    return PostResponse.builder()
                            .postDto(mapperService.mapToPostDto(post))
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

    @Override
    public ResponseEntity<GenericResponse> count(String authorizationToken, String groupId) {
        log.info("PostServiceImpl, count");

        long count = postRepository.countByGroupId(groupId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy số lượng bài viết thành công!")
                        .result(count)
                        .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getHotPosts(String authorizationToken, String groupId, int page, int size) {
        log.info("PostServiceImpl, getHotPosts");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("comment", "id", "post_id", "comments"),
                Aggregation.lookup("reaction", "id", "post_id", "reactions"),
                Aggregation.project("id", "content")
                        .and("comments").size().as("commentCount")
                        .and("reactions").size().as("reactionCount"),
                Aggregation.project("id", "content")
                        .and("totalInteractions").plus("commentCount").plus("reactionCount").as("totalInteractions"),
                Aggregation.sort(Sort.by(Sort.Order.desc("totalInteractions"))),
                Aggregation.limit(3)
        );

        AggregationResults<Post> results = mongoTemplate.aggregate(aggregation, "post", Post.class);
        List<PostDto> postDtos = results.getMappedResults().stream()
                .map(mapperService::mapToPostDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .success(true)
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy bài viết thành công!")
                        .result(postDtos)
                        .build());
    }

    public Boolean isUserInGroup(String userId, String groupId) {
        ResponseEntity<GenericResponse> responseEntity = groupClientService.validateUserInGroup(userId, groupId);
        return responseEntity.getStatusCode().equals(HttpStatus.OK);
    }

    public Reaction getReactionByUserIdInPost(String userId, Post post) {
        return reactionRepository.findByAuthorIdAndPostId(userId, post.getId()).orElse(null);
    }
}
