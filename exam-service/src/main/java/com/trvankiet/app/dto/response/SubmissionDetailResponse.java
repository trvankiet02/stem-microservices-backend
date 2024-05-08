package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.QuestionDto;
import com.trvankiet.app.dto.SubmissionDto;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SubmissionDetailResponse implements Serializable {

    private String id;
    private String question;
    private String userAnswer;
    private String correctAnswer;
    private Integer score;
    private String createdAt;
    private String updatedAt;

}
