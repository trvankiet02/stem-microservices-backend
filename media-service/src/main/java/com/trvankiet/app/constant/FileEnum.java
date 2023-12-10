package com.trvankiet.app.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum FileEnum {

    EXAM("exam", "Exam"),
    IMAGE("image", "Image"),
    VIDEO("video", "Video"),
    AUDIO("audio", "Audio"),
    DOCUMENT("document", "Document");


    private final String id;
    private final String name;
}
