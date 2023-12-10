package com.trvankiet.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
public class GroupConfigDto implements Serializable {

    private String id;
    private String code;
    private String type;
    private String accessibility;
    private String memberMode;
    private String description;
    private String createdAt;
    private String updatedAt;

}
