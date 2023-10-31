package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "albums")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Album implements Serializable {

    @Id
    @Field(name = "album_id")
    private String albumId;

    @Field(name = "user_id")
    private String userId;

    @Field(name = "album_name")
    private String albumName;

    @Field(name = "album_description")
    private String albumDescription;

    @Field(name = "accessibility")
    private String accessibility;

    @Field(name = "contributors")
    private List<String> contributorIds;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
