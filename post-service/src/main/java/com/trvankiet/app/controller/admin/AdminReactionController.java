package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reaction/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminReactionController {

    private final ReactionService reactionService;

    @GetMapping("/count")
    public ResponseEntity<GenericResponse> countReactionByGroupId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                                  @RequestParam("groupId") String groupId) {
        log.info("Count reaction by group id: {}", groupId);
        return reactionService.countReactionByGroupId(authorization, groupId);
    }
}
