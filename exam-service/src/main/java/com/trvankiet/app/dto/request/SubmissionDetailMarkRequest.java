package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionDetailMarkRequest {

    @NotBlank
    private String submissionDetailId;

    @NotBlank
    private Integer mark;

}
