package com.trvankiet.app.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateChatRoomRequest {

    private String name;
    private List<String> memberIds;
}
