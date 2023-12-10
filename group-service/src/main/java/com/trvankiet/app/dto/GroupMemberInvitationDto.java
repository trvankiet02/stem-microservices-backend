package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupMemberInvitationDto {

    private String id;
    @JsonProperty("inviter")
    private SimpleUserDto inviterDto;
    @JsonProperty("receiver")
    private SimpleUserDto receiverDto;
    private String state;
    private String createdAt;
    private String updatedAt;

}
