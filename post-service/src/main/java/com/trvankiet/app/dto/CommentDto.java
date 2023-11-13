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
public class CommentDto implements Serializable {

        private String id;
        @JsonProperty("author")
        private UserDto userDto;
        private String content;
        @JsonProperty("refUrls")
        private List<String> refUrls;
        @JsonProperty("reactions")
        private List<ReactionDto> reactionDtos;
        @JsonProperty("subComments")
        private List<CommentDto> subCommentDtos;
        private String createdAt;
        private String updatedAt;

}
