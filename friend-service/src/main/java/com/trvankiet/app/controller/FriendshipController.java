package com.trvankiet.app.controller;

import com.trvankiet.app.config.client.UserServiceClient;
import com.trvankiet.app.dto.response.FriendOfUserResponse;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Friendship;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/friendships")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<String>> getFriendIds(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("FriendshipController, getFriendIds");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendshipService.getFriendIds(userId);
    }

    @DeleteMapping
    public ResponseEntity<GenericResponse> deleteFriend(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestParam String friendId) {
        log.info("FriendshipController, deleteFriend");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendshipService.deleteFriend(userId, friendId);
    }

    @PostMapping
    public ResponseEntity<String> createFriendship(@RequestParam String userId) {
        log.info("FriendshipController, addFriend");
        return friendshipService.createFriendship(userId);
    }

    @GetMapping("/validate")
    public ResponseEntity<GenericResponse> validateFriendship(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestParam String friendId) {
        log.info("FriendshipController, validateFriendship");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendshipService.validateFriendship(userId, friendId);
    }

    @GetMapping("/list-friends")
    public ResponseEntity<List<FriendOfUserResponse>> getFriendsOfUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
                                                                       , @RequestParam String friendId) {
        log.info("FriendshipController, getFriendsOfUser");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendshipService.getFriendsOfUser(userId, friendId);
    }
}
