package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class SurveyDto {
    private String id;
    private SimpleUserDto author;
    private String groupId;
    private String content;
    private Boolean isMultipleChoice;
    private Boolean isAddOtherOption;
    @JsonProperty("options")
    private List<OptionDto> optionDtos;
    private String createdAt;
    private String updatedAt;
}
