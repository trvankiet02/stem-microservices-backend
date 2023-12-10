package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trvankiet.app.constant.StateType;
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

@Document(collection = "group_member_requests")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupMemberRequest implements Serializable {

    @Id
    @Field(name = "group_member_request_id")
    private String id;

    @DocumentReference
    @Field(name = "group_id")
    private Group group;

    @Field(name = "author_id")
    private String authorId;

    @Field(name = "state")
    private StateType state;

    @DocumentReference
    @Field(name = "group_member_invitation_id")
    private GroupMemberInvitation groupMemberInvitation;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
