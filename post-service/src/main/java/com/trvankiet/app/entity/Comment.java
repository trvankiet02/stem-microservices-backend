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
import java.util.List;

@Document(collection = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Comment implements Serializable {

    @Id
    @Field(name = "comment_id")
    private String commentId;

    @Field(name = "author_id")
    private Integer authorId;

    @Field(name = "content")
    private String content;

    @DocumentReference
    @Field(name = "reactions")
    private List<Reaction> reactions;

    @DocumentReference
    @Field(name = "sub_comments")
    private List<Comment> subComments;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
