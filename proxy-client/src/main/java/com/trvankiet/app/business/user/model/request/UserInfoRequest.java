package com.trvankiet.app.business.user.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserInfoRequest {

    @NotBlank(message = "Tên là bắt buộc!")
    private String firstName;

    @NotBlank(message = "Họ là bắt buộc!")
    private String lastName;

    private String phone;

    private Date dob;

    private String gender;

}
