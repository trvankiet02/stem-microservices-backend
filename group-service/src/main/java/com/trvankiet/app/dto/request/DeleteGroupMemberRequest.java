package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class DeleteGroupMemberRequest {
    private String userId;
    private String groupId;
}
