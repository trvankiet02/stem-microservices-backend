package com.trvankiet.app.service;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.dto.ChatMessageResult;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.ChatMessage;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ChatMessageService {
    ChatMessageDto saveChatMessageDto(ChatMessageDto chatMessageDto);

    ResponseEntity<GenericResponse> getAllUserMessages(String userId);

    ResponseEntity<GenericResponse> getMessageFromUser(String userId, String friendId, Integer page, Integer size);

    ResponseEntity<GenericResponse> getMessageFromGroup(String userId, String groupId, Integer page, Integer size);
}
