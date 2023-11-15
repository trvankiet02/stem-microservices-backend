package com.trvankiet.app.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAnswerDetailRequest {

    private String content;
    private Boolean isCorrect;

}
