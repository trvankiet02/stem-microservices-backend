package com.trvankiet.app.service;

import com.trvankiet.app.entity.Credential;

public interface EmailService {
    void sendVerificationEmail(Credential credential);
    void sendResetPasswordEmail(Credential credential);

}
