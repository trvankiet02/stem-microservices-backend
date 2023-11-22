package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.CommentDto;
import com.trvankiet.app.dto.ReactionDto;
import com.trvankiet.app.dto.UserDto;
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
public class PostDetailResponse implements Serializable {

    private String id;
    @JsonIgnore
    private String groupId;
    @JsonProperty("author")
    private UserDto userDto;
    private String content;
    private String type;
    private List<String> refUrls;
    @JsonProperty("reactions")
    private List<ReactionDto> reactionDtos;
    @JsonProperty("comments")
    private List<CommentDto> commentDtos;
    private String createdAt;
    private String updatedAt;

}
