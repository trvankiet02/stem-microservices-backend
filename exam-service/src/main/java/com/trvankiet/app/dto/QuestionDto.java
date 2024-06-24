package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class QuestionDto implements Serializable {

    private String id;
    private String content;
    @JsonBackReference
    @JsonProperty("exam")
    private ExamDto examDto;
    private String level;
    private String typeCode;
    private String createdAt;
    private String updatedAt;

}
