package com.trvankiet.app.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupAccessType {

    PUBLIC("public"),
    PRIVATE("private");

    private final String code;
}
