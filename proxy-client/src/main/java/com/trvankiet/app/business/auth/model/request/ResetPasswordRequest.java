package com.trvankiet.app.business.auth.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResetPasswordRequest implements Serializable {

    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

}
