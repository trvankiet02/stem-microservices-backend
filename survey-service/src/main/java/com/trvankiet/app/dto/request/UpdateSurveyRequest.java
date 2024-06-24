package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class UpdateSurveyRequest {
    private String content;
    private Boolean isMultipleChoice;
    private Boolean isAddOtherOption;
}
