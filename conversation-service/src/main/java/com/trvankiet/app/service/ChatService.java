package com.trvankiet.app.service;

import com.trvankiet.app.entity.ChatMessage;

public interface ChatService {
    ChatMessage sendPrivateMessage(ChatMessage chatMessage);
}
