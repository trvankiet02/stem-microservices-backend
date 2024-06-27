package com.trvankiet.app.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReportDto {

    private String id;
    private SimpleUserDto author;
    private String groupId;
    private String postId;
    private String content;
    private boolean isProcessed;
    private boolean isReportToAdmin;
    private boolean isReportToGroupManager;
    private String createdAt;
    private String updatedAt;

}
