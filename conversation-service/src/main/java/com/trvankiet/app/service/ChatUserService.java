package com.trvankiet.app.service;

import com.trvankiet.app.dto.UpdateUserRequest;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.entity.ChatUser;
import org.springframework.http.ResponseEntity;

public interface ChatUserService {
    ResponseEntity<ChatUser> createChatUser(UserDto userDto);

    ResponseEntity<ChatUser> updateChatUser(String id, UpdateUserRequest updateUserRequest);

    ResponseEntity<ChatUser> deleteChatUser(String id);
}
