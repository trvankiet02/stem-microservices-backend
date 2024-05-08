package com.trvankiet.app.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AddressDto implements Serializable {

    private Integer id;
    private String code;
    private String name;
    private String description;
    private String createdAt;
    private String updatedAt;

}
