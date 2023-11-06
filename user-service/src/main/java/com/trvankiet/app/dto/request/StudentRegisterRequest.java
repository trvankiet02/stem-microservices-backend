package com.trvankiet.app.dto.request;

import lombok.Data;

@Data
public class StudentRegisterRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private String phone;
    private String dob;
    private String province;
    private String district;
    private String school;
    private Integer grade;

}
