package com.trvankiet.app.service.impl;

import com.trvankiet.app.entity.ChatMessage;
import com.trvankiet.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    @Override
    public ChatMessage sendPrivateMessage(ChatMessage chatMessage) {
        log.info("ChatServiceImpl, sendPrivateMessage");
        return null;
    }
}
