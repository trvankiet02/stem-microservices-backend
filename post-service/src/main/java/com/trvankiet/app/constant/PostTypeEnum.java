package com.trvankiet.app.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostTypeEnum {

    POST("post"),
    QUESTION("question"),
    DISCUSSION("discussion");

    private final String code;

}
