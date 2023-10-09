package com.trvankiet.app.exception.wrapper;

public class TokenException extends RuntimeException{

    public TokenException() {
        super();
    }

    public TokenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TokenException(final String message) {
        super(message);
    }

    public TokenException(final Throwable cause) {
        super(cause);
    }
}
