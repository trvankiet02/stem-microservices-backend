package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChangeAvatarRequest {

    @NotNull
    private String groupId;
    @NotNull
    private MultipartFile avatar;

}
