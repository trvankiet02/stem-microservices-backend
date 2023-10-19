package com.trvankiet.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupCreateRequest {
    private String groupName;
    private String groupDescription;
    private String groupType;
}
