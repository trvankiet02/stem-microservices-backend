package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminPostController {

    private final PostService postService;

    @GetMapping("/count")
    public ResponseEntity<GenericResponse> count(@RequestHeader("Authorization") String authorizationToken,
                                                 @RequestParam("groupId") String groupId) {
        return postService.count(authorizationToken, groupId);
    }

    @GetMapping("/hot-posts")
    public ResponseEntity<GenericResponse> getHotPosts(@RequestHeader("Authorization") String authorizationToken,
                                                        @RequestParam("groupId") String groupId,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "3") int size) {
        return postService.getHotPosts(authorizationToken, groupId, page, size);
    }


}
