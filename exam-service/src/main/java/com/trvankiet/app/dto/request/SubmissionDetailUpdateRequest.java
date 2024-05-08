package com.trvankiet.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionDetailUpdateRequest {

    @NotNull
    private String id;
    @NotNull
    private String answer;

}
