package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExamSubmissionResponse {

    private String submissionId;
    @JsonProperty("questions")
    private List<QuestionSubmissionResponse> questionSubmissionResponses;
}
