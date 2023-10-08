package com.trvankiet.app.business.auth.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Role is required")
    private String role;

}
