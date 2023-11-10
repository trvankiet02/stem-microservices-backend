package com.trvankiet.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GroupCreateRequest {

    private String name;
    private String description;
    private String type;
    private String accessibility;
    private String memberMode;

}
