package com.trvankiet.app.service;

import com.trvankiet.app.dto.ChatMessageDto;
import com.trvankiet.app.dto.ChatMessageResult;
import com.trvankiet.app.dto.ChatRoomDto;
import com.trvankiet.app.dto.ChatUserDto;
import com.trvankiet.app.dto.NotificationDto;
import com.trvankiet.app.entity.ChatMessage;
import com.trvankiet.app.entity.ChatRoom;
import com.trvankiet.app.entity.ChatUser;
import com.trvankiet.app.entity.Notification;

public interface MapperService {
    ChatMessageDto mapToChatMessageDto(ChatMessage chatMessage);
    ChatMessageResult mapToChatMessageResult(ChatMessage chatMessage);
    ChatUserDto mapToChatUserDto(String id);
    ChatUserDto mapToChatUserDto(ChatUser chatUser);

    ChatRoomDto mapToChatRoomDto(ChatRoom chatRoom);
    NotificationDto mapToNotificationDto(Notification notification);

}
