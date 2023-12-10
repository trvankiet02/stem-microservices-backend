package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.SimpleUserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberResponse {

    private String id;
    @JsonProperty("user")
    private SimpleUserDto userDto;
    private Boolean isLocked;
    private String lockedAt;
    private String lockedReason;
    private String role;
    private String createdAt;
    private String updatedAt;

}
