package com.trvankiet.app.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String avatarUrl;

}
