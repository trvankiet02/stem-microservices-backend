package com.trvankiet.app.controller;

import com.trvankiet.app.dto.UpdateUserRequest;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat-users")
@RequiredArgsConstructor
@Slf4j
public class ChatUserController {

    private final ChatUserService chatUserService;

    @PostMapping
    public ResponseEntity<ChatUser> createChatUser(@RequestBody UserDto userDto) {
        log.info("ChatUserController: createChatUser");
        return chatUserService.createChatUser(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChatUser> updateChatUser(@PathVariable String id, @RequestBody UpdateUserRequest updateUserRequest) {
        log.info("ChatUserController: updateChatUser");
        return chatUserService.updateChatUser(id, updateUserRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ChatUser> deleteChatUser(@PathVariable String id) {
        log.info("ChatUserController: deleteChatUser");
        return chatUserService.deleteChatUser(id);
    }
}
