package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReactionDto implements Serializable {

    private String id;
    @JsonProperty("author")
    private SimpleUserDto userDto;
    private String type;
    private String createdAt;
    private String updatedAt;

}
