package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.StatusRequest;
import com.trvankiet.app.dto.request.UpdateChatUserRequest;
import com.trvankiet.app.dto.request.CreateChatUserRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.ChatUser;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChatUserService {
    ResponseEntity<ChatUser> createChatUser(CreateChatUserRequest createChatUserRequest);

    ResponseEntity<ChatUser> updateChatUser(String id, UpdateChatUserRequest updateChatUserRequest);

    ResponseEntity<ChatUser> deleteChatUser(String id);

    ChatUser saveChatUser(StatusRequest statusRequest);
    ChatUser disconnectChatUser(StatusRequest statusRequest);
    List<ChatUser> findOnlineChatUsers(List<String> userIds);

    ResponseEntity<GenericResponse> getOnlineFriends(String userId, List<String> friendIds);

    ResponseEntity<GenericResponse> getAllUserMessages(String userId, List<String> friends);

    ResponseEntity<GenericResponse> getLast10UserMessages(String userId, Integer page, Integer size);

    ResponseEntity<GenericResponse> getLast10GroupMessages(String userId, Integer page, Integer size);

    ResponseEntity<GenericResponse> getUserDetails(String id);
}
