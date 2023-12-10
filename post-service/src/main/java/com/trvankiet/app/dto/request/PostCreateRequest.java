package com.trvankiet.app.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostCreateRequest {

    private String content;
    private String typeName;
    private List<MultipartFile> mediaFiles;
    private String groupId;

}
