package com.trvankiet.app.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReactionTypeEnum {

    LIKE("like"),
    DISLIKE("dislike");

    private final String code;
}

