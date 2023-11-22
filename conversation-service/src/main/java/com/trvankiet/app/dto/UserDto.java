package com.trvankiet.app.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class UserDto {

    private String id;
    private String firstName;
    private String lastName;
    private String avatarUrl;

}
