package com.trvankiet.app.controller;

import com.trvankiet.app.entity.ChatMessage;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.service.ChatService;
import com.trvankiet.app.service.ChatUserService;
import com.trvankiet.app.service.client.FriendshipClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatUserService chatUserService;
    private final FriendshipClientService friendshipClientService;
    private final ChatService chatService;

    @MessageMapping("/user.addUser")
    @SendTo("/user/topic")
    public ChatUser addUser(@Payload ChatUser chatUser) {
        log.info("ChatController, addUser");
        chatUserService.saveChatUser(chatUser);
        return chatUser;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/topic")
    public ChatUser disconnectUser(@Payload ChatUser chatUser) {
        log.info("ChatController, disconnectUser");
        chatUserService.disconnectChatUser(chatUser);
        return chatUser;
    }

    @GetMapping("/users")
    public ResponseEntity<List<ChatUser>> findOnlineUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("ChatController, findOnlineUsers");
        ResponseEntity<List<String>> responseEntity = friendshipClientService.getFriendIds(authorizationHeader);
        if (responseEntity.getStatusCode().isError()) {
            return ResponseEntity.status(responseEntity.getStatusCode()).build();
        }
        return ResponseEntity.ok(chatUserService.findOnlineChatUsers(responseEntity.getBody()));
    }

    @MessageMapping("/private-message")
    public ChatMessage sendPrivateMessage(@Payload ChatMessage chatMessage) {
        log.info("ChatController, sendPrivateMessage");
        return chatService.sendPrivateMessage(chatMessage);
    }
}
