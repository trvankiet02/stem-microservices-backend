package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String description;

}
