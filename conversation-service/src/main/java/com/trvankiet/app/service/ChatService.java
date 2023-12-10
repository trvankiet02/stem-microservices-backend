package com.trvankiet.app.service;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.entity.ChatMessage;

public interface ChatService {
    ChatMessage saveChatMessageDto(ChatMessageDto chatMessageDto);

    ChatMessage saveChatRoomMessageDto(ChatMessageDto chatMessageDto, String roomId);

    void deleteChatMessage(ChatMessageDto chatMessageDto);
}
