package com.trvankiet.app.business.user.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private Date dob;
    private String gender;

}
