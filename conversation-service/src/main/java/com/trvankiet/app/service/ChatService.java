package com.trvankiet.app.service;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.dto.ChatMessageResult;
import com.trvankiet.app.entity.ChatMessage;

public interface ChatService {
    ChatMessageResult saveChatMessageDto(ChatMessageDto chatMessageDto);

    ChatMessageResult saveChatRoomMessageDto(ChatMessageDto chatMessageDto, String roomId);

    void deleteChatMessage(ChatMessageDto chatMessageDto);
}
