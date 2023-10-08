package com.trvankiet.app.business.user.model;

import com.trvankiet.app.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserDto implements Serializable {

    private String userId;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String coverImageUrl;
    private String email;
    private String phone;
    private Date dob;
    private Gender gender;
    private String about;
    private String address;
    private CredentialDto credential;

}
