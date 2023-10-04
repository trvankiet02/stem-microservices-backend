package com.trvankiet.app.business.auth.service.impl;

import com.trvankiet.app.business.user.model.CredentialDto;
import com.trvankiet.app.business.user.model.UserDetailsImpl;
import com.trvankiet.app.business.user.service.CredentialClientService;
import com.trvankiet.app.constant.AppConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CredentialClientService credentialClientService;
    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl, UserDetailsImpl, loadUserByUsername");
        return new UserDetailsImpl(this.credentialClientService.findByUsername(username));
    }

    public UserDetailsImpl loadUserById(String userId) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl, UserDetailsImpl, loadUserById");
        return new UserDetailsImpl(this.credentialClientService.findById(userId));
    }
}
