package com.trvankiet.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionRequest {

    @NotNull
    private String content;
    private String level;
    @NotNull
    private String typeCode;
    @NotNull
    private List<CreateAnswerRequest> answers;
}
