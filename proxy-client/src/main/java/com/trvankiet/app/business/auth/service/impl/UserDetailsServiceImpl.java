package com.trvankiet.app.business.auth.service.impl;

import com.trvankiet.app.business.user.model.UserDetailsImpl;
import com.trvankiet.app.business.user.service.client.CredentialClientService;
import com.trvankiet.app.business.user.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CredentialClientService credentialClientService;
    private final UserClientService userClientService;
    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl, UserDetailsImpl, loadUserByUsername");
        return new UserDetailsImpl(this.credentialClientService.findByUsername(username));
    }

    public UserDetailsImpl loadUserById(String userId) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl, UserDetailsImpl, loadUserById");
        return new UserDetailsImpl(this.userClientService.findByUserId(userId));
    }
}
