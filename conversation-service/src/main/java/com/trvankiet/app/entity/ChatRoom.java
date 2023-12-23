package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "chat_rooms")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatRoom implements Serializable {

    @Id
    @Field(name = "room_id")
    private String id;

    @Field(name = "author_id")
    private String authorId;

    @Field(name = "room_name")
    private String name;

    @Field(name = "room_avatarUrl")
    private String avatarUrl;

    @DocumentReference
    @Field(name = "chat_room_members")
    private List<ChatUser> members;

    @Field(name = "chat_room_is_accept_all_request")
    private Boolean isAcceptAllRequest;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
