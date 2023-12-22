package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveChatMemberRequest {

    @NotNull
    private String groupId;
    @NotNull
    private String userId;
}
