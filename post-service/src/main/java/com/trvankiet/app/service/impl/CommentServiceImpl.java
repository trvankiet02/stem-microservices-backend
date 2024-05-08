package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.CommentDto;
import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.SimpleUserDto;
import com.trvankiet.app.dto.request.CommentPostRequest;
import com.trvankiet.app.dto.request.UpdateCommentRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Comment;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.CommentRepository;
import com.trvankiet.app.repository.PostRepository;
import com.trvankiet.app.service.CommentService;
import com.trvankiet.app.service.MapperService;
import com.trvankiet.app.service.client.FileClientService;
import com.trvankiet.app.service.client.UserClientService;
import com.trvankiet.app.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MapperService mapperService;
    private final FileClientService fileClientService;
    @Override
    public ResponseEntity<GenericResponse> createComment(String userId, String authorizationHeader, CommentPostRequest commentPostRequest) {
        log.info("CommentServiceImpl, createComment({})", commentPostRequest);
        Post post = postRepository.findById(commentPostRequest.getPostId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết!"));
        List<FileDto> fileDtos = FileUtil.isValidMultipartFiles(commentPostRequest.getMediaFiles()) ?
                fileClientService.uploadCommentFiles(authorizationHeader,
                        commentPostRequest.getMediaFiles(), post.getGroupId()) : new ArrayList<>();
        Date now = new Date();
        Comment comment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .authorId(userId)
                .post(post)
                .content(commentPostRequest.getContent())
                .subComments(new ArrayList<>())
                .refUrls(fileDtos.stream().map(FileDto::getRefUrl).toList())
                .createdAt(now)
                .build();
        commentRepository.save(comment);
        postRepository.save(post);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Tạo bình luận thành công!")
                .result(mapperService.mapToCommentDto(comment))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateComment(String userId, String commentId, String authorizationHeader, UpdateCommentRequest updateCommentRequest) {
        log.info("CommentServiceImpl, updateComment({})", updateCommentRequest);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bình luận!"));
        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa bình luận này!");
        }
        List<FileDto> fileDtos = FileUtil.isValidMultipartFiles(updateCommentRequest.getMediaFiles()) ?
                fileClientService.uploadCommentFiles(authorizationHeader, updateCommentRequest.getMediaFiles(),
                        comment.getPost().getGroupId()) : new ArrayList<>();
        comment.setContent(updateCommentRequest.getContent());
        if (!fileDtos.isEmpty()) {
            comment.setRefUrls(fileDtos.stream().map(FileDto::getRefUrl).toList());
        }
        comment.setUpdatedAt(new Date());
        commentRepository.save(comment);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật bình luận thành công!")
                .result(mapperService.mapToCommentDto(comment))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getComments(String postId, int page, int size) {
        log.info("CommentServiceImpl, getComments({})", postId);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        List<Comment> comments = commentRepository.findAllByPostId(postId, pageable).toList();
        List<CommentDto> commentDtos = comments.stream()
                .map(mapperService::mapToCommentDto).toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách bình luận thành công!")
                .result(commentDtos)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteComment(String userId, String commentId) {
        log.info("CommentServiceImpl, deleteComment({})", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bình luận!"));
        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa bình luận này!");
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Xóa bình luận thành công!")
                .result(null)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> createRepComment(String userId, String commentId, String authorizationHeader, UpdateCommentRequest updateCommentRequest) {
        log.info("CommentServiceImpl, createRepComment()");
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bình luận!"));
        List<FileDto> fileDtos = FileUtil.isValidMultipartFiles(updateCommentRequest.getMediaFiles()) ?
                fileClientService.uploadCommentFiles(authorizationHeader, updateCommentRequest.getMediaFiles(),
                        comment.getPost().getGroupId()) : new ArrayList<>();
        Date now = new Date();
        Comment repComment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .authorId(userId)
                .content(updateCommentRequest.getContent())
                .subComments(new ArrayList<>())
                .refUrls(fileDtos.stream().map(FileDto::getRefUrl).toList())
                .createdAt(now)
                .build();
        comment.getSubComments().add(repComment);
        commentRepository.saveAll(List.of(comment, repComment));
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Trả lời bình luận thành công!")
                .result(mapperService.mapToCommentDto(repComment))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> countCommentByGroupId(String authorizationToken, String groupId) {
        log.info("CommentServiceImpl, countCommentByGroupId({})", groupId);

        long count = postRepository.findAllByGroupId(groupId).stream()
                .mapToLong(post -> commentRepository.countByPostId(post.getId()))
                .sum();

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Đếm bình luận thành công!")
                .result(count)
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}
