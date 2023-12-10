package com.trvankiet.app.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionDetailUpdateRequest {

    private String id;
    private String answer;

}
