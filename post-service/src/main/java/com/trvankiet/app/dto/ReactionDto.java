package com.trvankiet.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReactionDto implements Serializable {

        private String reactionId;
        private UserDto userDto;
        private String type;
        private String createdAt;
        private String updatedAt;

}
