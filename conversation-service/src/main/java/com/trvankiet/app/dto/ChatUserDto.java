package com.trvankiet.app.dto;

import com.trvankiet.app.constant.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ChatUserDto {

    private String id;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private StatusEnum status;
    private Date lastOnline;

}
