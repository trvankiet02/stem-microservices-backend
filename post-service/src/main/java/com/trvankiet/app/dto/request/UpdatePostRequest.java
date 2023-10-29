package com.trvankiet.app.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdatePostRequest {

    private String postId;
    private String content;
    private String postType;
    private List<MultipartFile> mediaFiles;

}
