package com.trvankiet.app.util;

import com.trvankiet.app.entity.Token;
import com.trvankiet.app.exception.wrapper.TokenException;

public class TokenUtil {
    public static boolean tokenIsNotExpiredAndRevoked(Token token) {
        if (token == null)
            throw new TokenException("Token rỗng!");
        return !token.getExpired() && !token.getRevoked();
    }
}