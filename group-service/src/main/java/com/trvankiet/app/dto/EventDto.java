package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EventDto {
    private String eventId;
    @JsonProperty("author")
    private UserDto userDto;
    private String eventName;
    private String eventDescription;
    private String startDate;
    private String endDate;
}
