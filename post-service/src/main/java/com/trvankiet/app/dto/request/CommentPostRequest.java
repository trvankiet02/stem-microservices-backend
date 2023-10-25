package com.trvankiet.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CommentPostRequest {

    private String content;
    private List<MultipartFile> mediaFiles;
    private Long postId;
    private Long userId;

}
