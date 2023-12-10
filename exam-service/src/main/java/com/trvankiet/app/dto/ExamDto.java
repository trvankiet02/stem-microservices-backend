package com.trvankiet.app.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ExamDto implements Serializable {

        private String id;
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
        private String createdAt;
        private String updatedAt;

}
