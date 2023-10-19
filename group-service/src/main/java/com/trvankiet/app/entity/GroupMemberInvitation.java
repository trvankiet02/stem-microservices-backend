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

@Document(collection = "group_member_invitation")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupMemberInvitation implements Serializable {

    @Id
    @Field(name = "group_member_invitation_id")
    private String groupMemberInvitationId;

    @Field(name = "from_user_id")
    private String fromUserId;

    @Field(name = "to_user_id")
    private String toUserId;

    @Field(name = "state")
    private String state;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}