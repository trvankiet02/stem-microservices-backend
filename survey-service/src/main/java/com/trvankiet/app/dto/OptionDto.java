package com.trvankiet.app.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OptionDto {
    private String id;
    private String content;
    private SimpleUserDto author;
    private Integer voteCount;
    private boolean isUserVoted;
    private String createdAt;
    private String updatedAt;
}
