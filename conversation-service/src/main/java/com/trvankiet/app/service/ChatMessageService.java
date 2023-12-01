package com.trvankiet.app.service;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.entity.ChatMessage;

import java.util.Optional;

public interface ChatMessageService {
    ChatMessage saveChatMessageDto(ChatMessageDto chatMessageDto);

    Optional<ChatMessage> findById(String id);

    void save(ChatMessage chatMessage);
}
