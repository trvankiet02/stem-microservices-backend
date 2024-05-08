package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class CreateChatUserRequest {

    private String id;
    private String firstName;
    private String lastName;
    private String avatarUrl;

}
