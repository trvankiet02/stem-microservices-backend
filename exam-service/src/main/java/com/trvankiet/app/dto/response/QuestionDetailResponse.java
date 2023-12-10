package com.trvankiet.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.AnswerDto;
import com.trvankiet.app.dto.QuestionDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionDetailResponse {

    @JsonProperty("question")
    QuestionDto questionDto;
    @JsonProperty("answers")
    List<AnswerDto> answerDtos;

}
