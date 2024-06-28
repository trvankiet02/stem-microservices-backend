package com.trvankiet.app.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "post-service", contextId = "postClientService", path = "/api/v1/posts")

public interface PostClientService {

    @GetMapping("/getGroupId/{postId}")
    ResponseEntity<String> getGroupId(@PathVariable String postId);
}
