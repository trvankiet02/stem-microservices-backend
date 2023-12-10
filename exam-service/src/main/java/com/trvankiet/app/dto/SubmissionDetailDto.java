package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.entity.Submission;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@Builder
public class SubmissionDetailDto implements Serializable {

    private String id;
    private String answer;
    private Integer score;
    @JsonBackReference
    @JsonProperty("question")
    private QuestionDto questionDto;
    @JsonBackReference
    @JsonProperty("submission")
    private SubmissionDto submissionDto;
    private String createdAt;
    private String updatedAt;

}
