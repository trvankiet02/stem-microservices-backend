package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAnswerDetailRequest {

    @NotNull
    private String content;
    @NotNull
    private Boolean isCorrect;

}
