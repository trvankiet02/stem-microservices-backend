package com.trvankiet.app.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateQuestionDetailRequest {

    private String content;
    private String level;
    private String typeCode;

}
