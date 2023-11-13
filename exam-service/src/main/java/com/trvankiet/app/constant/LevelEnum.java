package com.trvankiet.app.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LevelEnum {

    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private final String level;

}
