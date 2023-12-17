package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.CreateChatRoomRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface ChatRoomService {
    ResponseEntity<GenericResponse> createChatRoom(String userId, CreateChatRoomRequest createChatRoomRequest);

    ResponseEntity<GenericResponse> getChatRoom(String userId, String id);

    ResponseEntity<GenericResponse> getMember(String userId, String groupId);
}
