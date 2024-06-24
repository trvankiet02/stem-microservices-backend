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

@Document(collection = "surveys")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Survey implements Serializable {

    @Id
    @Field(name = "survey_id")
    private String id;

    @Field(name = "group_id")
    private String groupId;

    @Field(name = "author_id")
    private String authorId;

    @Field(name = "survey_content")
    private String content;

    @Builder.Default
    @Field(name = "survey_is_multiple_choice")
    private Boolean isMultipleChoice = false;

    @Builder.Default
    @Field(name = "survey_is_add_other_option")
    private Boolean isAddOtherOption = false;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
