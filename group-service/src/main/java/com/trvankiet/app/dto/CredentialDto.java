package com.trvankiet.app.dto;

import com.trvankiet.app.constant.Provider;
import com.trvankiet.app.constant.RoleBasedAuthority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

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