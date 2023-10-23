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

@Document(collection = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Post implements Serializable {

    @Id
    @Field(name = "post_id")
    private String postId;

    @Field(name = "group_id")
    private String groupId;

    @Field(name = "author_id")
    private String authorId;

    @Field(name = "accessibility")
    private String accessibility;

    @DocumentReference
    @Field(name = "post_type")
    private PostType postType;

    @Field(name = "post_content")
    private String content;

    @DocumentReference
    @Field(name = "reactions")
    private List<Reaction> reactions;

    @DocumentReference
    @Field(name = "comments")
    private List<Comment> comments;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
