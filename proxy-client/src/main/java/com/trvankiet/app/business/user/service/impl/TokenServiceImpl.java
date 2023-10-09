package com.trvankiet.app.business.user.service.impl;
import com.trvankiet.app.business.user.model.request.EmailRequest;
import com.trvankiet.app.business.user.service.TokenService;
import com.trvankiet.app.business.user.service.client.TokenClientService;
import com.trvankiet.app.constant.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenClientService tokenClientService;
    @Override
    public ResponseEntity<GenericResponse> resetPassword(EmailRequest emailRequest) {
        log.info("TokenServiceImpl, ResponseEntity<GenericResponse>, resetPassword");
        return tokenClientService.resetPassword(emailRequest);
    }
}
