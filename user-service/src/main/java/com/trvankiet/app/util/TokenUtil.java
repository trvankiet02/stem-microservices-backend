package com.trvankiet.app.util;

import com.trvankiet.app.entity.Token;
import com.trvankiet.app.exception.wrapper.BadRequestException;

public class TokenUtil {

    public static boolean tokenIsExpiredOrRevoked(Token token) {
        if (token == null)
            return false;
        return token.getIsRevoked() || token.getIsExpired();
    }
}
