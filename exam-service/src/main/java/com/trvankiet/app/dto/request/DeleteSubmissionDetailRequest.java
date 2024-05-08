package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteSubmissionDetailRequest {

    @NotNull
    private String submissionDetailId;

}
