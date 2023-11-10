package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupMemberInvitationDto {

    private String id;
    @JsonProperty("group")
    private GroupDto groupDto;
    @JsonProperty("inviter")
    private UserDto inviterDto;
    @JsonProperty("receiver")
    private UserDto receiverDto;
    private String state;
    private String createdAt;
    private String updatedAt;

}
