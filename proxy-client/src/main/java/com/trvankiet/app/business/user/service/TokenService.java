package com.trvankiet.app.business.user.service;

import com.trvankiet.app.business.user.model.request.EmailRequest;
import com.trvankiet.app.constant.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface TokenService {
    ResponseEntity<GenericResponse> resetPassword(EmailRequest emailRequest);
}
