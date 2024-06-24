package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateExamByExcelRequest {
    @NotNull
    private String groupId;
    @NotNull
    private String name;
    private String description;
    @NotNull
    private Integer duration; // minutes
    @NotNull
    private String startedAt;
    @NotNull
    private String endedAt;
    @NotNull
    private Boolean isEnabled;
    @NotNull
    private Boolean isAutoMark;
    private String level;
    @NotNull
    private MultipartFile multipartFile;
}
