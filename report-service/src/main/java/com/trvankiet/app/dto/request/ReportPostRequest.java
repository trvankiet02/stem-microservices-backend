package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class ReportPostRequest {

    private String postId;
    private String reason;
    private String groupId;
    private Boolean isReportToAdmin;
    private Boolean isReportToGroupManager;

}
