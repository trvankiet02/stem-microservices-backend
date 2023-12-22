package com.trvankiet.app.controller;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.dto.ChatMessageResult;
import com.trvankiet.app.dto.NotificationDto;
import com.trvankiet.app.dto.request.StatusRequest;
import com.trvankiet.app.entity.ChatMessage;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.service.ChatService;
import com.trvankiet.app.service.ChatUserService;
import com.trvankiet.app.service.client.FriendshipClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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
    public ChatUser addUser(@Payload StatusRequest statusRequest) {
        log.info("ChatController, addUser");
        return chatUserService.saveChatUser(statusRequest);
    }

    @MessageMapping("/user.disconnectUser")
    public ChatUser disconnectUser(@Payload StatusRequest statusRequest) {
        log.info("ChatController, disconnectUser");
        return chatUserService.disconnectChatUser(statusRequest);
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
    public ChatMessageDto sendPrivateMessage(@Payload ChatMessageDto chatMessageDto) {
        log.info("ChatController, sendPrivateMessage");
        return chatService.saveChatMessageDto(chatMessageDto);
    }

    @MessageMapping("/room-message/{roomId}")
    public ChatMessageDto sendRoomMessage(@Payload ChatMessageDto chatMessageDto, @DestinationVariable String roomId) {
        log.info("ChatController, sendRoomMessage");
        return chatService.saveChatRoomMessageDto(chatMessageDto, roomId);
    }

    @MessageMapping("/delete-message")
    public void deleteChatMessage(@Payload ChatMessageDto chatMessageDto) {
        log.info("ChatController, deleteChatMessage");
        chatService.deleteChatMessage(chatMessageDto);
    }
    
    @MessageMapping("/userNotify/{userId}")
	public void sendNotification(@Payload NotificationDto notificationDto, @DestinationVariable String userId) {
		log.info("ChatController, sendNotification");
		chatService.saveNotification(notificationDto, userId);
	}

}
