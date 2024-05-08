package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.ChatUserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMemberResponse {

    @JsonProperty("user")
    private ChatUserDto chatUserDto;
    private boolean isOwner;

}
