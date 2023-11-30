package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendResponse {

    @JsonProperty("user")
    private UserDto userDto;
    private Boolean isFriend;

}
