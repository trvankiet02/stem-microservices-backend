package com.trvankiet.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ChatMessageDto {

    private String id;
    private String senderId;
    private String senderFirstName;
    private String senderLastName;
    private String senderAvatarUrl;
    private String refUrl;
    private Boolean isDeleted;
    private String receiverId;
    private String chatRoomId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    
}
