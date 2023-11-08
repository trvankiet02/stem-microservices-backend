package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class GroupConfigRequest {

    private String type;
    private String accessibility;
    private String memberMode;

}
