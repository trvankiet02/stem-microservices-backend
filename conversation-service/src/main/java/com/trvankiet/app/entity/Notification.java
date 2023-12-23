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

@Document(collection = "chat_notifications")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Notification implements Serializable {

    @Id
    @Field(name = "notification_id")
    private String id;

    @DocumentReference
    @Field(name = "receiver_id")
    private ChatUser receiver;
    
    @Field(name = "notification_avatar_url")
    private String avatarUrl;
    
    
    @Field(name = "notification_post_id")
    private String postId;
    
    @Field(name = "notification_comment_id")
    private String commentId;
    
    @Field(name = "notification_group_id")
    private String groupId;
    
    @Field(name = "notification_chat_room_id")
    private String chatRoomId;
    
    
    @Field(name = "notification_content")
    private String content;
    
    @Field(name = "norification_ref_url")
    private String refUrl;
    
    @Field(name = "notification_is_readed")
    private Boolean isReaded;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;
    
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
