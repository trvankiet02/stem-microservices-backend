package com.trvankiet.app.service;

import com.trvankiet.app.dto.FileDto;
import com.trvankiet.app.dto.request.CommentPostRequest;
import com.trvankiet.app.dto.request.UpdateCommentRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {
    ResponseEntity<GenericResponse> createComment(String userId, String authorizationHeader, CommentPostRequest commentPostRequest);

    ResponseEntity<GenericResponse> updateComment(String userId, String commentId, String authorizationHeader, UpdateCommentRequest updateCommentRequest);

    ResponseEntity<GenericResponse> getComments(String postId, int page, int size);

    ResponseEntity<GenericResponse> deleteComment(String userId, String commentId);

    ResponseEntity<GenericResponse> createRepComment(String userId, String commentId, String authorizationHeader, UpdateCommentRequest updateCommentRequest);

    ResponseEntity<GenericResponse> countCommentByGroupId(String authorizationToken, String groupId);
}
