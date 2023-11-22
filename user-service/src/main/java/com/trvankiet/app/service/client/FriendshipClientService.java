package com.trvankiet.app.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "friend-service", contextId = "friendshipClientService", path = "/api/v1/friendships")
public interface FriendshipClientService {

    @GetMapping
    ResponseEntity<List<String>> getFriendIds(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);
}
