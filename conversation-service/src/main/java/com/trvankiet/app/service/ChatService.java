package com.trvankiet.app.service;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.dto.ChatMessageResult;
import com.trvankiet.app.dto.NotificationDto;
import com.trvankiet.app.entity.ChatMessage;

public interface ChatService {
    ChatMessageDto saveChatMessageDto(ChatMessageDto chatMessageDto);

    ChatMessageDto saveChatRoomMessageDto(ChatMessageDto chatMessageDto, String roomId);

    void deleteChatMessage(ChatMessageDto chatMessageDto);

	void saveNotification(NotificationDto notificationDto, String userId);
}
