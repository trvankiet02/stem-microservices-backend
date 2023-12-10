package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class UpdateChatUserRequest {

    private String firstName;
    private String lastName;
    private String avatarUrl;

}
