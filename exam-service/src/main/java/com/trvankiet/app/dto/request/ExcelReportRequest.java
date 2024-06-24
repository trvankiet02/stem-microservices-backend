package com.trvankiet.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Valid
public class ExcelReportRequest {
    @NotNull
    private String groupId;
    @NotNull
    private MultipartFile multipartFile;
}
