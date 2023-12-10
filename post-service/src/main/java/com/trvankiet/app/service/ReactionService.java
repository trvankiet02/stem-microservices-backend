package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.ReactionRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface ReactionService {
    ResponseEntity<GenericResponse> reactPost(String userId, ReactionRequest reactionRequest);

    ResponseEntity<GenericResponse> deleteReaction(String userId, String reactionId);

    ResponseEntity<GenericResponse> getReactionsByPostId(String postId);
}
