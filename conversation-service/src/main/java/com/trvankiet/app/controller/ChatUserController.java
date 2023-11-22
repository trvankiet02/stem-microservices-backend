package com.trvankiet.app.controller;

import com.trvankiet.app.dto.request.UpdateChatUserRequest;
import com.trvankiet.app.dto.request.CreateChatUserRequest;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat-users")
@RequiredArgsConstructor
@Slf4j
public class ChatUserController {

    private final ChatUserService chatUserService;

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
}
