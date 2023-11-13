package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class CreateAnswerRequest {

    private String content;
    private Boolean isCorrect;

}
