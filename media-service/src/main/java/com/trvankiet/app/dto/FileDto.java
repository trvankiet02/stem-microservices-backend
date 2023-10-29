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
public class FileDto implements Serializable {

    private String fileId;
    private String authorId;
    private String fileLink;
    private String fileType;
}
