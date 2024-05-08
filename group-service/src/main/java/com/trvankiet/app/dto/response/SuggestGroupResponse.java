package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.GroupDto;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SuggestGroupResponse implements Serializable {

    @JsonProperty("group")
    private GroupDto groupDto;
    public Integer memberCount;
    private Boolean isMember;

}
