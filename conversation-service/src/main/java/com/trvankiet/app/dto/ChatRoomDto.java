package com.trvankiet.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ChatRoomDto {
    private String id;
    private String name;
    private String avatarUrl;
    private Date createdAt;
    private Date updatedAt;
}
