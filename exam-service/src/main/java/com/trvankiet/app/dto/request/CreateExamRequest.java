package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateExamRequest {

    @NotNull
    private String groupId;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private Integer duration; // minutes
    @NotNull
    private String startedAt;
    @NotNull
    private String endedAt;
    @NotNull
    private Boolean isEnabled;
    @NotNull
    private Integer numberOfQuestion;
    private String level;
    @NotNull
    private Integer maxScore;
    @NotNull
    private List<CreateQuestionRequest> questions;

}
