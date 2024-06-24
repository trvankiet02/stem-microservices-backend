package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateQuestionDetailRequest {

    @NotNull
    private String content;
    private String level;
    @NotNull
    private String typeCode;

}
