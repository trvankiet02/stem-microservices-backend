package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trvankiet.app.constant.FriendType;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "friends")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Friend implements Serializable {

    @Id
    @Field(name = "friend_id")
    private String friendId;

    @Field(name = "friend_type")
    private String friendType;

    @Field(name = "messages")
    private List<ObjectId> messages;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
