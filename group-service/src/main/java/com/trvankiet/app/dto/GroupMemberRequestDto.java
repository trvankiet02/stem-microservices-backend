package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupMemberRequestDto {

    private String id;
    @JsonProperty("group")
    private GroupDto groupDto;
    @JsonProperty("user")
    private UserDto userDto;
    private String state;
    @JsonProperty("invitation")
    private GroupMemberInvitationDto invitationDto;
    private String createdAt;
    private String updatedAt;

}
