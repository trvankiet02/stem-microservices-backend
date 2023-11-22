package com.trvankiet.app.service.client;

import com.trvankiet.app.dto.FriendRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "friend-service", contextId = "friendRequestClientService", path = "/api/v1/friend-requests")
public interface FriendRequestClientService {

    @GetMapping
    ResponseEntity<List<FriendRequestDto>> getFriendRequests(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);
}
