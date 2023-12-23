package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping("/count")
    public ResponseEntity<GenericResponse> count(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken,
                                                 @RequestParam("groupId") String groupId) {
        log.info("count comment");
        return commentService.countCommentByGroupId(authorizationToken, groupId);
    }
}
