package com.trvankiet.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "group_roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupMemberRole implements Serializable {

    @Id
    @Field(name = "role_id")
    private String roleId;

    @Field(name = "role_name")
    private String roleName;

    @Field(name = "role_description")
    private String roleDescription;

}
