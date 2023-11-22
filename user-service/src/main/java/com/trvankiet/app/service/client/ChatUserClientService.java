package com.trvankiet.app.service.client;

import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.CreateChatUserRequest;
import com.trvankiet.app.dto.request.UpdateChatUserRequest;
import com.trvankiet.app.dto.response.ChatUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "conversation-service", contextId = "chatUserClientService", path = "/api/v1/chat-users")
public interface ChatUserClientService {

    @PostMapping
    ResponseEntity<ChatUser> createChatUser(@RequestBody CreateChatUserRequest createChatUserRequest);

    @PutMapping("/{id}")
    ResponseEntity<ChatUser> updateChatUser(@PathVariable String id, @RequestBody UpdateChatUserRequest updateChatUserRequest);

    @DeleteMapping("/{id}")
    ResponseEntity<ChatUser> deleteChatUser(@PathVariable String id);

}
