package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class ParentRegisterRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private String phone;
    private String dob;

}
