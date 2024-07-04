package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompetitionCreateRequest {

    @NotNull(message = "name is required")
    private String name;
    private String description;
    private String subject;

}
