package com.trvankiet.app.service;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.request.CommentPostRequest;
import com.trvankiet.app.dto.request.UpdateCommentRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {
    ResponseEntity<GenericResponse> createComment(String userId, List<FileDto> fileDtos, CommentPostRequest commentPostRequest);

    ResponseEntity<GenericResponse> updateComment(String userId, String commentId, List<FileDto> fileDtos, UpdateCommentRequest updateCommentRequest);

    ResponseEntity<GenericResponse> getComments(String postId, int page, int size);

    ResponseEntity<GenericResponse> deleteComment(String userId, String commentId);
}
