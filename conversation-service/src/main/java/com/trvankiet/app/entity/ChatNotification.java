package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
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
public class ChatNotification implements Serializable {

    @Id
    @Field(name = "notification_id")
    private String id;

    @DocumentReference
    @Field(name = "receiver_id")
    private ChatUser receiver;

    @Field(name = "notification_content")
    private String content;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;
}
