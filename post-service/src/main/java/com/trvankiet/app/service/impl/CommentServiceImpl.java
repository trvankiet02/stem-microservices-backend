package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.CommentDto;
import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.request.CommentPostRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Comment;
import com.trvankiet.app.entity.Post;
import com.trvankiet.app.exception.wrapper.ForbiddenException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.CommentRepository;
import com.trvankiet.app.repository.PostRepository;
import com.trvankiet.app.service.CommentService;
import com.trvankiet.app.service.MapperService;
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
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MapperService mapperService;
    @Override
    public ResponseEntity<GenericResponse> createComment(String userId, String postId, List<FileDto> fileDtos, CommentPostRequest commentPostRequest) {
        log.info("CommentServiceImpl, createComment({})", commentPostRequest);
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết!"));
        Date now = new Date();
        Comment comment = Comment.builder()
                .commentId(UUID.randomUUID().toString())
                .authorId(userId)
                .content(commentPostRequest.getContent())
                .reactions(new ArrayList<>())
                .subComments(new ArrayList<>())
                .filesId(fileDtos.stream().map(FileDto::getFileLink).toList())
                .createdAt(now)
                .build();
        post.getComments().add(comment);
        commentRepository.save(comment);
        postRepository.save(post);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Tạo bình luận thành công!")
                .result(mapperService.mapToPostDto(post))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateComment(String userId, String commentId, List<FileDto> fileDtos, CommentPostRequest commentPostRequest) {
        log.info("CommentServiceImpl, updateComment({})", commentPostRequest);
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bình luận!"));
        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa bình luận này!");
        }
        comment.setContent(commentPostRequest.getContent());
        comment.setFilesId(fileDtos.stream().map(FileDto::getFileLink).toList());
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
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết!"));
        List<Comment> comments = post.getComments();
        int total = comments.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        List<Comment> subComments = comments.subList(start, end);
        List<CommentDto> commentDtos = subComments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
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
        Comment comment = commentRepository.findByCommentId(commentId)
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
}
