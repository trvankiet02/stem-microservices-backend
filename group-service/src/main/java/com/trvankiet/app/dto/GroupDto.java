package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trvankiet.app.entity.Event;
import com.trvankiet.app.entity.GroupMember;
import com.trvankiet.app.entity.GroupMemberRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupDto implements Serializable {

    private String groupId;
    private String groupName;
    private String groupDescription;
    private String groupImage;
    private String groupType;

}
