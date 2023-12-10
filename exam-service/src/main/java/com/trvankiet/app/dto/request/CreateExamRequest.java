package com.trvankiet.app.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateExamRequest {

    private String groupId;
    private String name;
    private String description;
    private Integer duration; // minutes
    private String staredAt;
    private String endedAt;
    private Boolean isEnabled;
    private Integer numberOfQuestion;
    private String level;
    private Integer maxScore;
    private List<CreateQuestionRequest> questions;

}
