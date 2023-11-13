package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupCreateRequest {

    private String name;
    private String description;
    @NotNull(message = "typeCode is required")
    private String typeName;
    private String accessibilityName;
    private String memberModeName;
    private String subject;
    private Integer grade;

}
