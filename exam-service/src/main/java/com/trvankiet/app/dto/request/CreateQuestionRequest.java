package com.trvankiet.app.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionRequest {

    private String content;
    private String level;
    private String typeCode;
    private List<CreateAnswerRequest> answers;
}
