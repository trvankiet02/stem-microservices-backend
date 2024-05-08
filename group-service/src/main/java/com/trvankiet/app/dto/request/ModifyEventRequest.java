package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class ModifyEventRequest {
    private String eventName;
    private String eventDescription;
    private String startDate;
    private String endDate;
}
