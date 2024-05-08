package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ToGroupMemberRequest {
    @NotBlank(message = "groupId is required")
    private String groupId;
}
