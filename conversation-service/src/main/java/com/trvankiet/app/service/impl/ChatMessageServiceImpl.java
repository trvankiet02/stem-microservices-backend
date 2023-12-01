package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.entity.ChatMessage;
import com.trvankiet.app.entity.ChatRoom;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.repository.ChatMessageRepository;
import com.trvankiet.app.repository.ChatRoomRepository;
import com.trvankiet.app.repository.ChatUserRepository;
import com.trvankiet.app.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ChatMessage saveChatMessageDto(ChatMessageDto chatMessageDto) {
        log.info("ChatMessageServiceImpl, saveChatMessageDto");
        ChatMessage chatMessage;
        if (chatMessageDto.getId() != null) {
            chatMessage = chatMessageRepository.findById(chatMessageDto.getId()).orElse(ChatMessage.builder().build());
        }
        else {
            chatMessage = ChatMessage.builder().build();
        }
        chatMessage.setContent(chatMessageDto.getContent());
        if (chatMessageDto.getSenderId() != null) {
            Optional<ChatUser> optionalSender = chatUserRepository.findById(chatMessageDto.getSenderId());
            optionalSender.ifPresent(chatMessage::setSender);
        }
        if (chatMessageDto.getChatRoomId() != null) {
            Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatMessageDto.getChatRoomId());
            optionalChatRoom.ifPresent(chatMessage::setChatRoom);
        }
        if (chatMessageDto.getReceiverId() != null) {
            Optional<ChatUser> optionalReceiver = chatUserRepository.findById(chatMessageDto.getReceiverId());
            optionalReceiver.ifPresent(chatMessage::setReceiver);
        }
        chatMessage.setRefUrl(chatMessageDto.getRefUrl() != null ? chatMessageDto.getRefUrl() : null);
        chatMessage.setIsDeleted(chatMessageDto.getIsDeleted() != null ? chatMessageDto.getIsDeleted() : false);
        chatMessage.setCreatedAt(chatMessageDto.getCreatedAt() != null ? chatMessageDto.getCreatedAt() : null);
        chatMessage.setUpdatedAt(chatMessageDto.getUpdatedAt() != null ? chatMessageDto.getUpdatedAt() : null);
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public Optional<ChatMessage> findById(String id) {
        return chatMessageRepository.findById(id);
    }
}
