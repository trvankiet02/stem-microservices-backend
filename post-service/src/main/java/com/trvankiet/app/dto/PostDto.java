package com.trvankiet.app.dto;

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

        private String postId;
        private String groupId;
        @JsonProperty("user")
        private UserDto userDto;
        private String accessibility;
        private String content;
        private String postType;
        private List<String> mediaUrls;
        private List<ReactionDto> reactions;
        private List<CommentDto> comments;
        private String createdAt;
        private String updatedAt;
}
