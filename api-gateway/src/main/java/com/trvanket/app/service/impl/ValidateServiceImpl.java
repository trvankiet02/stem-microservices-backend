package com.trvanket.app.service.impl;

import com.trvanket.app.dto.CredentialDto;
import com.trvanket.app.jwt.service.JwtService;
import com.trvanket.app.service.ValidateService;
import com.trvanket.app.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final UserClientService userClientService;
    private final JwtService jwtService;


    boolean isValid(CredentialDto credentialDto) {
        return credentialDto.getIsEnabled() &&
                credentialDto.getIsAccountNonExpired() &&
                credentialDto.getIsAccountNonLocked() &&
                credentialDto.getIsCredentialsNonExpired();
    }

    @Override
    public boolean isValidUser(String accessToken) {
        String userId = jwtService.extractUserId(accessToken);
        CredentialDto credentialDto = userClientService.getCredentialDto(userId);
        return isValid(credentialDto);
    }

    @Override
    public boolean isValidAdmin(String accessToken) {
        String userId = jwtService.extractUserId(accessToken);
        CredentialDto credentialDto = userClientService.getCredentialDto(userId);
        if (isValid(credentialDto)) {
            return credentialDto.getRole().equals("ROLE_ADMIN");
        }
        return false;
    }
}
