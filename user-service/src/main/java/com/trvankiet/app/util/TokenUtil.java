package com.trvankiet.app.util;

import com.trvankiet.app.entity.Token;
import com.trvankiet.app.exception.wrapper.TokenException;
import com.trvankiet.app.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public class TokenUtil {

    public static boolean tokenIsNotExpiredAndRevoked(Token token) {
        if (token == null)
            throw new TokenException("Token rỗng!");
        return !token.getExpired() && !token.getRevoked();
    }
}
