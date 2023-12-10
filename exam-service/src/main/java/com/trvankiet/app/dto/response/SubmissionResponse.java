package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.SubmissionDto;
import com.trvankiet.app.entity.Submission;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class SubmissionResponse implements Serializable {

    @JsonProperty("submission")
    SubmissionDto submissionDto;
    @JsonProperty("submissionDetail")
    List<SubmissionDetailResponse> submissionDetailResponses;
}
