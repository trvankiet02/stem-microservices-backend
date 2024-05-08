package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateChatRoomRequest {

    @NotNull
    private String name;
    private List<String> memberIds;
    @NotNull
    private Boolean isAcceptAllRequest;
}
