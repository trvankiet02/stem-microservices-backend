package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.entity.ChatUser;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ChatMessageResult {

        private String id;
        @JsonProperty("sender")
        private ChatUserDto sender;
        private String refUrl;
        private Boolean isDeleted;
        @JsonProperty("receiver")
        private ChatUserDto receiver;
        private String chatRoomId;
        private String content;
        private Date createdAt;
        private Date updatedAt;
}
