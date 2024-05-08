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
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "submission_details")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubmissionDetail implements Serializable {

    @Id
    @Field(name = "submission_id")
    private String id;

    @Field(name = "submission_detail_answer")
    private String answer;

    @Field(name = "submission_detail_score")
    private Integer score;

    @DocumentReference
    @Field(name = "question_id")
    private Question question;

    @DocumentReference
    @Field(name = "submission_id")
    private Submission submission;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
