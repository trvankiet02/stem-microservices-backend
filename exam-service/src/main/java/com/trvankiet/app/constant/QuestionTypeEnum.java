package com.trvankiet.app.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionTypeEnum {

    SINGLE_CHOICE("single_choice"),
    MULTIPLE_CHOICE("multiple_choice"),
    TRUE_FALSE("true_false"),
    ESSAY("essay");

    private final String code;

}
