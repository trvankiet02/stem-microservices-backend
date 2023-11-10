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

@Document(collection = "group_configs")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupConfig implements Serializable {

    @Id
    @Field(name = "group_config_id")
    private String id;

    @Field(name = "group_config_code")
    private String code;

    @Field(name = "group_config_type")
    private String type;

    @Field(name = "group_config_accessibility")
    private String accessibility;

    @Field(name = "group_config_member_mode")
    private String memberMode;

    @Field(name = "group_config_description")
    private String description;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
