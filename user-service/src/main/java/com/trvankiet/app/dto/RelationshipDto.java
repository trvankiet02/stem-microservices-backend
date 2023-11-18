package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelationshipDto {

    private String id;
    @JsonProperty("parent")
    private UserDto parentDto;
    @JsonProperty("student")
    private UserDto studentDto;
    private boolean isAccepted;

}
