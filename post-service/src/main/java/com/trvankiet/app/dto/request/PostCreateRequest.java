package com.trvankiet.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostCreateRequest {

    private String content;
    private String postType;
    private List<MultipartFile> mediaFiles;
    private String groupId;

}
