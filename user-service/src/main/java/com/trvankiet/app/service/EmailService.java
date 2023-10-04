package com.trvankiet.app.service;

import com.trvankiet.app.entity.Credential;

public interface EmailService {
    public void sendVerificationEmail(Credential credential);
}
