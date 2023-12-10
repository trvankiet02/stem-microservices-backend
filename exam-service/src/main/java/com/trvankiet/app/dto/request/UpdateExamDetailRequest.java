package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class UpdateExamDetailRequest {

    private String name;
    private String description;
    private Integer duration; // minutes
    private String staredAt;
    private String endedAt;
    private Boolean isEnabled;
    private Integer numberOfQuestion;
    private String level;
    private Integer maxScore;

}
