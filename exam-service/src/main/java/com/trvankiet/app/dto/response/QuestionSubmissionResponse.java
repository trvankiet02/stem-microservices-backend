package com.trvankiet.app.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class QuestionSubmissionResponse {

    private String submissionDetailId;
    private String content;
    private String level;
    private String typeCode;
    List<AnswerSubmissionResponse> answers;
}
