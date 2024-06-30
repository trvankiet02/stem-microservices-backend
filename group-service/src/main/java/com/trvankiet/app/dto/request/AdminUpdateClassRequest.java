package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class AdminUpdateClassRequest {

    private String classId;
    private String name;
    private String description;
    private Boolean isPublic;
    private Boolean isAcceptAllRequest;

    private String subject;
    private Integer grade;
    private Integer yearFrom;
    private Integer yearTo;
}
