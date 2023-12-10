package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostDto implements Serializable {

        private String id;
        @JsonIgnore
        private String groupId;
        private String authorId;
        private String authorFirstName;
        private String authorLastName;
        private String authorAvatar;
        private String content;
        private String type;
        private List<String> refUrls;
        private long totalReactions;
        private long totalComments;
        @JsonProperty("comments")
        private List<CommentDto> commentDtos;
        private String createdAt;
        private String updatedAt;

}
