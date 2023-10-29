package com.trvankiet.app.util;

import com.trvankiet.app.entity.Token;
import com.trvankiet.app.exception.wrapper.BadRequestException;

public class TokenUtil {

    public static boolean tokenIsNotExpiredAndRevoked(Token token) {
        if (token == null)
            throw new BadRequestException("Token rỗng!");
        return !token.getExpired() && !token.getRevoked();
    }
}
