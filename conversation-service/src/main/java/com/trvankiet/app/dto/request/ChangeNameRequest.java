package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeNameRequest {

    @NotNull
    private String groupId;
    @NotNull
    private String name;
}
