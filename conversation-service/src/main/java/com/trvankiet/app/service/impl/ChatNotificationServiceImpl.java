package com.trvankiet.app.service.impl;

import com.trvankiet.app.repository.ChatNotificationRepository;
import com.trvankiet.app.service.ChatNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatNotificationServiceImpl implements ChatNotificationService {
    private final ChatNotificationRepository chatNotificationRepository;
}
