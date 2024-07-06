package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class ReportPostRequest {

    private String postId;
    private String reason;
    private Boolean isReportToAdmin;
    private Boolean isReportToGroupManager;

}
