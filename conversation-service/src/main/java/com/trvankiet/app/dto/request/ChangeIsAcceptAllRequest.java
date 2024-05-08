package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeIsAcceptAllRequest {

    @NotNull
    private String groupId;
    @NotNull
    private Boolean isAcceptAllRequest;

}
