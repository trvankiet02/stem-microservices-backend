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

@Document(collection = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Group implements Serializable {

    @Id
    @Field(name = "group_id")
    private String groupId;

    @Field(name = "group_name")
    private String groupName;

    @Field(name = "group_description")
    private String groupDescription;

    @Field(name = "group_image")
    private String groupImage;

    @Field(name = "group_type")
    private String groupType;

    @Field(name = "group_member_mode")
    private String groupMemberMode;

    @DocumentReference
    @Field(name = "group_members")
    private List<GroupMember> groupMembers;

    @DocumentReference
    @Field(name = "group_events")
    private List<Event> groupEvents;

    @DocumentReference
    @Field(name = "group_member_requests")
    private List<GroupMemberRequest> groupMemberRequests;

    @DocumentReference
    @Field(name = "group_member_invitations")
    private List<GroupMemberRequest> groupMemberInvitations;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
