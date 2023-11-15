package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SubmissionDto implements Serializable {

    private String id;
    private String authorId;
    private String startedAt;
    private String endedAt;
    private Float score;
    @JsonBackReference
    @JsonProperty("exam")
    private ExamDto examDto;
    private String createdAt;
    private String updatedAt;

}
