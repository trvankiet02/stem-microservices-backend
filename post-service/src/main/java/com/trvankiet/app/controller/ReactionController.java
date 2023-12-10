package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.ReactionRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reactions")
@Slf4j
@RequiredArgsConstructor
public class ReactionController {
    private final JwtService jwtService;
    private final ReactionService reactionService;

    @PutMapping
    public ResponseEntity<GenericResponse> reactPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                     @RequestBody ReactionRequest reactionRequest) {
        log.info("ReactionController, reactPost({})", reactionRequest);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return reactionService.reactPost(userId, reactionRequest);
    }

    @DeleteMapping("/{reactionId}")
    public ResponseEntity<GenericResponse> deleteReaction(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                          @PathVariable("reactionId") String reactionId) {
        log.info("ReactionController, deleteReaction({})", reactionId);
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return reactionService.deleteReaction(userId, reactionId);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<GenericResponse> getReactionsByPostId(@PathVariable("postId") String postId) {
        log.info("ReactionController, getReactionsByPostId({})", postId);
        return reactionService.getReactionsByPostId(postId);
    }

}
