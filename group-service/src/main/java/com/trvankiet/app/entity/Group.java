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

@Document(collection = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Group implements Serializable {

    @Id
    @Field(name = "group_id")
    private String id;

    @Field(name = "group_name")
    private String name;

    @Field(name = "group_description")
    private String description;

    @Field(name = "author_id")
    private String authorId;

    @Builder.Default
    @Field(name = "group_avatar_url")
    private String avatarUrl = "";

    @Builder.Default
    @Field(name = "group_cover_url")
    private String coverUrl = "";

    @Field(name = "isClass")
    private Boolean isClass;

    @Field(name = "isCompetition")
    private Boolean isCompetition;
    
    @Field(name = "isPublic")
    private Boolean isPublic;

    @Field(name = "isAcceptAllRequest")
    private Boolean isAcceptAllRequest;

    @Field(name = "class_subject")
    private String subject;

    @Field(name = "class_grade")
    private Integer grade;
    
    @Field(name = "class_year_from")
    private Integer yearFrom;
    
    @Field(name = "class_year_to")
    private Integer yearTo;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
