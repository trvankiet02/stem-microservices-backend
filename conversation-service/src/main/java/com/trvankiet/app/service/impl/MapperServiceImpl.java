package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.dto.ChatMessageResult;
import com.trvankiet.app.dto.ChatRoomDto;
import com.trvankiet.app.dto.ChatUserDto;
import com.trvankiet.app.entity.ChatMessage;
import com.trvankiet.app.entity.ChatRoom;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.repository.ChatUserRepository;
import com.trvankiet.app.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapperServiceImpl implements MapperService {
    private final ChatUserRepository chatUserRepository;
    @Override
    public ChatMessageDto mapToChatMessageDto(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .id(chatMessage.getId())
                .senderId(chatMessage.getSender().getId())
                .refUrl(chatMessage.getRefUrl() == null ? null : chatMessage.getRefUrl())
                .isDeleted(chatMessage.getIsDeleted() != null && chatMessage.getIsDeleted())
                .receiverId(chatMessage.getReceiver() == null ? null : chatMessage.getReceiver().getId())
                .chatRoomId(chatMessage.getChatRoom() == null ? null : chatMessage.getChatRoom().getId())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .updatedAt(chatMessage.getUpdatedAt() == null ? null : chatMessage.getUpdatedAt())
                .build();
    }

    @Override
    public ChatMessageResult mapToChatMessageResult(ChatMessage chatMessage) {
        return ChatMessageResult.builder()
                .id(chatMessage.getId())
                .sender(mapToChatUserDto(chatMessage.getSender()))
                .refUrl(chatMessage.getRefUrl() == null ? null : chatMessage.getRefUrl())
                .isDeleted(chatMessage.getIsDeleted() != null && chatMessage.getIsDeleted())
                .receiver(chatMessage.getReceiver() == null ? null : mapToChatUserDto(chatMessage.getSender()))
                .chatRoomId(chatMessage.getChatRoom() == null ? null : chatMessage.getChatRoom().getId())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt() == null ? null : chatMessage.getCreatedAt())
                .updatedAt(chatMessage.getUpdatedAt() == null ? null : chatMessage.getUpdatedAt())
                .build();
    }

    @Override
    public ChatUserDto mapToChatUserDto(ChatUser chatUser) {
        return ChatUserDto.builder()
                .id(chatUser.getId())
                .firstName(chatUser.getFirstName())
                .lastName(chatUser.getLastName())
                .avatarUrl(chatUser.getAvatarUrl() == null ? null : chatUser.getAvatarUrl())
                .status(chatUser.getStatus())
                .lastOnline(chatUser.getLastOnline() == null ? null : chatUser.getLastOnline())
                .build();
    }

    @Override
    public ChatRoomDto mapToChatRoomDto(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .createdAt(chatRoom.getCreatedAt() == null ? null : chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt() == null ? null : chatRoom.getUpdatedAt())
                .build();
    }

    @Override
    public ChatUserDto mapToChatUserDto(String id) {
        ChatUser chatUser = chatUserRepository.findById(id).orElse(null);
        if (chatUser == null) {
            return null;
        }
        return ChatUserDto.builder()
                .id(chatUser.getId())
                .firstName(chatUser.getFirstName())
                .lastName(chatUser.getLastName())
                .avatarUrl(chatUser.getAvatarUrl() == null ? null : chatUser.getAvatarUrl())
                .status(chatUser.getStatus())
                .lastOnline(chatUser.getLastOnline() == null ? null : chatUser.getLastOnline())
                .build();
    }
}
