package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.UpdateChatUserRequest;
import com.trvankiet.app.dto.request.CreateChatUserRequest;
import com.trvankiet.app.entity.ChatUser;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChatUserService {
    ResponseEntity<ChatUser> createChatUser(CreateChatUserRequest createChatUserRequest);

    ResponseEntity<ChatUser> updateChatUser(String id, UpdateChatUserRequest updateChatUserRequest);

    ResponseEntity<ChatUser> deleteChatUser(String id);

    void saveChatUser(ChatUser chatUser);
    void disconnectChatUser(ChatUser chatUser);
    List<ChatUser> findOnlineChatUsers(List<String> userIds);
}
