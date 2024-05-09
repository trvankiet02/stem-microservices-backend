package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionDetailMarkRequest {

    @NotNull
    private String submissionDetailId;

    @NotNull
    private Integer mark;

}
