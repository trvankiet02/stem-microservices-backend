package com.trvankiet.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostCreateRequest {

    private String content;
    private String postType;
    private MultipartFile files;
    private String groupId;

}
