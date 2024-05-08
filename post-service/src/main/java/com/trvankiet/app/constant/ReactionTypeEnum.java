package com.trvankiet.app.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReactionTypeEnum {

    LIKE("like"),
    DISLIKE("dislike"),
	USEFUL("useful"),
	DOUBTFUL("doubtful");

    private final String code;
}

