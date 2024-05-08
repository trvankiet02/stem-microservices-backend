package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.entity.Question;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AnswerDto implements Serializable {

    private String id;
    private String content;
    private Boolean isCorrect;
    @JsonBackReference
    @JsonProperty("question")
    private QuestionDto questionDto;
    private String createdAt;
    private String updatedAt;

}
