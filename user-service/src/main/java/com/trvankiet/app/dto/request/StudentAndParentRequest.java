package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class StudentAndParentRequest {

    private StudentRegisterRequest student;
    private ParentRegisterRequest parent;

}
