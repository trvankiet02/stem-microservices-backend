package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "exams")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Exam implements Serializable {

    @Id
    @Field("exam_id")
    private String id;

    @Field("group_id")
    private String groupId;

    @Field("exam_name")
    private String name;

    @Field("exam_description")
    private String description;

    @Field("exam_duration")
    private Integer duration; // minutes

    @Field("exam_stared_at")
    private Date staredAt;

    @Field("exam_ended_at")
    private Date endedAt;

    @Field("exam_is_enabled")
    private Boolean isEnabled;

    @Field("exam_level")
    private String level;

    @Field("exam_max_score")
    private Integer maxScore;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
