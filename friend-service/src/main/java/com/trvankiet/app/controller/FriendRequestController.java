package com.trvankiet.app.controller;

import com.trvankiet.app.dto.FriendRequestDto;
import com.trvankiet.app.dto.request.CreateFriendRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.FriendRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friend-requests")
@RequiredArgsConstructor
@Slf4j
public class FriendRequestController {

    private final JwtService jwtService;
    private final FriendRequestService friendRequestService;

    @GetMapping
    public ResponseEntity<List<FriendRequestDto>> getFriendRequests(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("FriendRequestController, getFriendRequests");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendRequestService.getFriendRequests(userId);
    }

    @PostMapping
    public ResponseEntity<GenericResponse> createFriendRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                @RequestBody @Valid CreateFriendRequest createFriendRequest) {
        log.info("FriendRequestController, createFriendRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendRequestService.createFriendRequest(userId, createFriendRequest);
    }

    @PutMapping("/accept/{friendRequestId}")
    public ResponseEntity<GenericResponse> acceptFriendRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                @PathVariable String friendRequestId) {
        log.info("FriendRequestController, acceptFriendRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendRequestService.acceptFriendRequest(userId, friendRequestId);
    }

    @PutMapping("/reject/{friendRequestId}")
    public ResponseEntity<GenericResponse> rejectFriendRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                @PathVariable String friendRequestId) {
        log.info("FriendRequestController, rejectFriendRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendRequestService.rejectFriendRequest(userId, friendRequestId);
    }

    @DeleteMapping("/{friendRequestId}")
    public ResponseEntity<GenericResponse> deleteFriendRequest(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                @PathVariable String friendRequestId) {
        log.info("FriendRequestController, deleteFriendRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendRequestService.deleteFriendRequest(userId, friendRequestId);
    }

}
