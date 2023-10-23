package com.trvankiet.app.dto.response;

import com.trvankiet.app.constant.Gender;
import com.trvankiet.app.dto.CredentialDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProfileResponse implements Serializable {

    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String coverImageUrl;
    private String email;
    private String phone;
    private String dob;
    private String gender;
    private String about;
    private String workedAt;
    private String address;

}
