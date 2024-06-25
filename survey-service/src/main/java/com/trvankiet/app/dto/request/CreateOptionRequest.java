package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOptionRequest {
    @NotNull
    private String surveyId;
    @NotNull
    @NotEmpty
    private String content;

}
