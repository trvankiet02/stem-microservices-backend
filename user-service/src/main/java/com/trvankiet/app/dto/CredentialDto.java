package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trvankiet.app.constant.Provider;
import com.trvankiet.app.constant.RoleBasedAuthority;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CredentialDto implements Serializable {

    private String credentialId;
    private Provider provider;
    private String username;
    private String password;
    private RoleBasedAuthority roleBasedAuthority;
    private Boolean isEnabled;
    private Boolean isAccountNonExpired;
    private Boolean isAccountNonLocked;
    private Boolean isCredentialsNonExpired;
    private Date lockedAt;
    private String lockedReason;

}
