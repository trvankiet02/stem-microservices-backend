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

@Document(collection = "files")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class File implements Serializable {

    @Id
    @Field(name = "file_id")
    private String fileId;

    @Field(name = "author_id")
    private String authorId;

    @Field(name = "content")
    private String content;

    @Field(name = "file_link")
    private String fileLink;

    @DocumentReference
    @Field(name = "album_id")
    private Album album;

    @DocumentReference
    @Field(name = "file_type")
    private FileType fileType;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
