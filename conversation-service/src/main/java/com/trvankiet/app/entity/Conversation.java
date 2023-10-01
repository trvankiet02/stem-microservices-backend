package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Conversation  implements Serializable {

    @Id
    @Field(name = "conversation_id")
    private String conversationId;

    @Field(name = "conversation_name")
    private String conversationName;

    @Field(name = "conversation_image")
    private String conversationImage;

    @DocumentReference
    @Field(name = "conversation_members")
    private List<ConversationMember> conversationMembers;

    @Field("conversation_messages")
    private List<String> conversationMessages;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
