package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class AdminCreateClassRequest {

    private String name;
    private String description;
    private String authorId;
    private Boolean isPublic;
    private Boolean isAcceptAllRequest;

    private String subject;
    private Integer grade;
    private Integer yearFrom;
    private Integer yearTo;
}
