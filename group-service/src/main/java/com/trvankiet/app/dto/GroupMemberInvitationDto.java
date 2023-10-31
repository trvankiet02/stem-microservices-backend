package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class GroupMemberInvitationDto {

    private String id;
    @JsonProperty("group")
    private GroupDto groupDto;
    @JsonProperty("inviter")
    private UserDto userDto;
    @JsonProperty("createdDate")
    private String createdDate;

}
