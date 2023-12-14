package com.trvankiet.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAnswerRequest {

    @NotNull
    private String content;
    @NotNull
    private Boolean isCorrect;

}
