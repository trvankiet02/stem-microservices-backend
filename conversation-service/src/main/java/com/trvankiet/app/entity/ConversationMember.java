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
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "conversation_members")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ConversationMember implements Serializable {

    @Id
    @Field(name = "conversation_member_id")
    private String conversationMemberId;

    @Field(name = "conversation_member_name")
    private String conversationMemberName;

    @Field(name = "user_id")
    private Integer userId;

    @Field(name = "conversation_member_role")
    private String conversationMemberRole;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
