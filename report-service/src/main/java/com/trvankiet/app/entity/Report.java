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

@Document(collection = "reports")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Report implements Serializable {

    @Id
    @Field(name = "report_id")
    private String id;

    @Field(name = "report_author_id")
    private String authorId;

    @Field(name = "report_group_id")
    private String groupId;

    @Field(name = "report_post_id")
    private String postId;

    @Field(name = "report_content")
    private String content;

    @Field(name = "report_is_processed")
    private Boolean isProcessed;

    @Field(name = "report_is_report_to_admin")
    private Boolean isReportToAdmin;

    @Field(name = "report_is_report_to_group_manager")
    private Boolean isReportToGroupManager;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;


}
