package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.PostDto;
import com.trvankiet.app.dto.ReactionDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse {

    @JsonProperty("post")
    private PostDetailResponse postDetailResponse;

    @JsonProperty("reaction")
    private ReactionDto reactionDto;
}
