package com.trvankiet.app.constant;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponse implements Serializable {

    private Boolean success;
    private String message;
    private Object result;
    private int statusCode;

}
