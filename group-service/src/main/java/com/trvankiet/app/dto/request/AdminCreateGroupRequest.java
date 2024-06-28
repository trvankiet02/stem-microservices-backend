package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class AdminCreateGroupRequest {

    private String name;
    private String description;
    private String authorId;
    private Boolean isPublic;
    private Boolean isAcceptAllRequest;

}
