package com.trvankiet.app.business.user.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {

    private final CredentialDto credentialDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.credentialDto.getRoleBasedAuthority().name()));
    }

    @Override
    public String getPassword() {
        return credentialDto.getPassword();
    }

    @Override
    public String getUsername() {
        return credentialDto.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return credentialDto.getIsAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return credentialDto.getIsAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialDto.getIsCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return credentialDto.getIsEnabled();
    }

}
