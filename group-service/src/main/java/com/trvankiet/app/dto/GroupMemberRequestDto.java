package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupMemberRequestDto {
    @JsonProperty("id")
    private String groupMemberRequestId;
    private GroupDto groupDto;
    @JsonProperty("user")
    private UserDto userDto;
    private String createdDate;
}
