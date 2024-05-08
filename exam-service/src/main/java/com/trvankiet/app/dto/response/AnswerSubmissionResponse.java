package com.trvankiet.app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerSubmissionResponse {

    private String answer;
    private boolean isChecked;

}
