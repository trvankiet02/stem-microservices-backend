package com.trvankiet.app.service.client;

import com.trvankiet.app.dto.FriendRequestDto;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "friend-service", contextId = "friendshipClientService", path = "/api/v1/friendships")
public interface FriendshipClientService {

    @GetMapping
    ResponseEntity<List<String>> getFriendIds(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);

    @PostMapping
    ResponseEntity<String> createFriendship(@RequestParam String userId);
}
