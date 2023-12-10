package com.trvankiet.app.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateCommentRequest {

    private String content;
    private List<MultipartFile> mediaFiles;

}
