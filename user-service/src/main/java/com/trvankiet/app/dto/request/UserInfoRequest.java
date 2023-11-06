package com.trvankiet.app.dto.request;

import com.trvankiet.app.constant.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;
import java.util.List;

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
    private String province;
    private String district;
    private String school;
    private List<String> subjects;

}
