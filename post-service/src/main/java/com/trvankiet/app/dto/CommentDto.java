package com.trvankiet.app.dto;

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

        private String commentId;
        private UserDto userDto;
        private String content;
        private List<ReactionDto> reactions;
        private List<CommentDto> subComments;
        private String createdAt;
        private String updatedAt;

}
