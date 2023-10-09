package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.repository.TokenRepository;
import com.trvankiet.app.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TokenRepository tokenRepository;
    @Override
    @Async
    public void sendVerificationEmail(Credential credential) {
        log.info("EmailServiceImpl, void, sendVerificationEmail, credential");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
            Token verificationToken = Token.builder()
                    .token(UUID.randomUUID().toString())
                    .type(TokenType.VERIFICATION_TOKEN)
                    .revoked(false)
                    .expired(false)
                    .expiredAt(expirationTime)
                    .credential(credential)
                    .build();

            helper.setTo(credential.getUsername());
            String mailContent = "<p>Click vào <a href=\"http://localhost:3000/verify/" + verificationToken.getToken()
                    + "\">đây</a> để xác thực tài khoản của bạn trước " + verificationToken.getExpiredAt().toString() +"</p>";
            helper.setText(mailContent, true);
            helper.setSubject("Liên kết xác thực cho tài khoản của bạn trên hệ thống STEM!");
            mailSender.send(message);

            List<Token> existingEmailVerificationToken = tokenRepository.findByCredential(credential)
                    .stream()
                    .filter(token -> token.getType().equals(TokenType.VERIFICATION_TOKEN))
                    .toList();
            tokenRepository.deleteAll(existingEmailVerificationToken);
            tokenRepository.save(verificationToken);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email: ", e);
        }
    }

    @Override
    @Async
    public void sendResetPasswordEmail(Credential credential) {
        log.info("EmailServiceImpl, void, sendResetPasswordEmail, credential");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
            Token resetPasswordToken = Token.builder()
                    .token(UUID.randomUUID().toString())
                    .type(TokenType.RESET_PASSWORD_TOKEN)
                    .revoked(false)
                    .expired(false)
                    .expiredAt(expirationTime)
                    .credential(credential)
                    .build();

            helper.setTo(credential.getUsername());
            String mailContent = "<p>Click vào <a href=\"http://localhost:3000/forgot-password/" + resetPasswordToken.getToken()
                    + "\">đây</a> để thực hiện nhập mật khẩu mới của bạn trước " + resetPasswordToken.getExpiredAt().toString() + "</p>";
            helper.setText(mailContent, true);
            helper.setSubject("Liên kết đặt lại mật khẩu cho tài khoản của bạn trên hệ thống STEM!");
            mailSender.send(message);

            List<Token> existingResetPasswordToken = tokenRepository.findByCredential(credential)
                    .stream()
                    .filter(token -> token.getType().equals(TokenType.RESET_PASSWORD_TOKEN))
                    .toList();
            tokenRepository.deleteAll(existingResetPasswordToken);
            tokenRepository.save(resetPasswordToken);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email: ", e);
        }
    }
}
