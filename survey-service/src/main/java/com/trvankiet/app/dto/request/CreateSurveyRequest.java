package com.trvankiet.app.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateSurveyRequest {

    private String groupId;
    private String content;
    private Boolean isMultipleChoice;
    private Boolean isAddOtherOption;
    private List<String> options;

}
