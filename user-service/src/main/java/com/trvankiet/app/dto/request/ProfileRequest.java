package com.trvankiet.app.dto.request;

import com.trvankiet.app.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProfileRequest implements Serializable {

    private String firstName;
    private String lastName;
    private String phone;
    private String dob;
    private String gender;

}
