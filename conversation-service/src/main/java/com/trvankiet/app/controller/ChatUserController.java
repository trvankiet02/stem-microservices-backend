package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.UpdateChatUserRequest;
import com.trvankiet.app.dto.request.CreateChatUserRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.ChatUserService;
import com.trvankiet.app.service.client.FriendshipClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat-users")
@RequiredArgsConstructor
@Slf4j
public class ChatUserController {

    private final ChatUserService chatUserService;
    private final JwtService jwtService;
    private final FriendshipClientService friendshipClientService;

    @PostMapping
    public ResponseEntity<ChatUser> createChatUser(@RequestBody CreateChatUserRequest createChatUserRequest) {
        log.info("ChatUserController: createChatUser");
        return chatUserService.createChatUser(createChatUserRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChatUser> updateChatUser(@PathVariable String id, @RequestBody UpdateChatUserRequest updateChatUserRequest) {
        log.info("ChatUserController: updateChatUser");
        return chatUserService.updateChatUser(id, updateChatUserRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ChatUser> deleteChatUser(@PathVariable String id) {
        log.info("ChatUserController: deleteChatUser");
        return chatUserService.deleteChatUser(id);
    }

    @GetMapping("/get-online-friends")
    public ResponseEntity<GenericResponse> getOnlineFriends(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        List<String> friendIds = friendshipClientService.getFriendIds(token).getBody();
        return chatUserService.getOnlineFriends(userId, friendIds);
    }

    @GetMapping("/get-user-messages")
    public ResponseEntity<GenericResponse> getAllUserMessages(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        List<String> friends = friendshipClientService.getFriendIds(token).getBody();
        return chatUserService.getAllUserMessages(userId, friends);
    }
}
