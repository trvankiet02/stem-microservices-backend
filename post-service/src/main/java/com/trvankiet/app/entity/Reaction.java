	package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trvankiet.app.constant.ReactionTypeEnum;
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

@Document(collection = "reactions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Reaction implements Serializable {

    @Id
    @Field(name = "reaction_id")
    private String id;

    @Field(name = "author_id")
    private String authorId;

    @DocumentReference
    @Field(name = "post_id")
    private Post post;

    @Field(name = "reaction_type")
    private ReactionTypeEnum type;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
