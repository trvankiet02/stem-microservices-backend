package com.trvankiet.app.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class SubCommentRequest {

    private Long commentId;
    private String content;
    private List<MultipartFile> mediaFiles;
}
