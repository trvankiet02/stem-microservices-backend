package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.dto.ChatMessageResult;
import com.trvankiet.app.entity.ChatMessage;
import com.trvankiet.app.repository.ChatMessageRepository;
import com.trvankiet.app.service.ChatMessageService;
import com.trvankiet.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessageResult saveChatMessageDto(ChatMessageDto chatMessageDto) {
        log.info("ChatServiceImpl, saveChatMessageDto");
        ChatMessageResult chatMessage = chatMessageService.saveChatMessageDto(chatMessageDto);
        simpMessagingTemplate.convertAndSendToUser(
                chatMessageDto.getReceiverId(),
                "/private",
                chatMessage);
        return chatMessage;
    }

    @Override
    public ChatMessageResult saveChatRoomMessageDto(ChatMessageDto chatMessageDto, String roomId) {
        log.info("ChatServiceImpl, saveChatRoomMessageDto");
        ChatMessageResult chatMessage = chatMessageService.saveChatMessageDto(chatMessageDto);
        simpMessagingTemplate.convertAndSend("/chatroom/room/" + roomId, chatMessage);
        return chatMessage;
    }

    @Override
    public void deleteChatMessage(ChatMessageDto chatMessageDto) {
        log.info("ChatServiceImpl, deleteChatMessage");
        Optional<ChatMessage> optionalChatMessage = chatMessageRepository.findById(chatMessageDto.getId());
        optionalChatMessage.ifPresent(chatMessage -> {
            chatMessage.setIsDeleted(chatMessageDto.getIsDeleted() != null ? chatMessageDto.getIsDeleted() : false);
            chatMessageRepository.save(chatMessage);
        });
        simpMessagingTemplate.convertAndSendToUser(
                chatMessageDto.getSenderId(),
                "/private",
                chatMessageDto);
    }
}
