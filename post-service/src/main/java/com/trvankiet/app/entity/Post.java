package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trvankiet.app.constant.PostTypeEnum;
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
    private String id;

    @Field(name = "group_id")
    private String groupId;

    @Field(name = "author_id")
    private String authorId;

    @Field(name = "post_type")
    private PostTypeEnum type;

    @Field(name = "post_content")
    private String content;

    @Field(name = "post_is_public")
    private Boolean isPublic;

    @Field(name = "ref_urls")
    private List<String> refUrls;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
