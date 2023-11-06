package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.constant.Gender;
import com.trvankiet.app.entity.Credential;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
public class UserDto implements Serializable {

    private String firstName;
    private String lastName;
    private String role;
    private String gender;
    private String email;
    private String phone;
    private Date dob;
    private String avatarUrl;
    private String coverUrl;
    @JsonProperty("credential")
    private CredentialDto credentialDto;
    private Date createdAt;
    private Date updatedAt;

}
