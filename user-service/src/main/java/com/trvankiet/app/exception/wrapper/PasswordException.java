package com.trvankiet.app.exception.wrapper;

public class PasswordException extends RuntimeException {

    public PasswordException() {
        super();
    }

    public PasswordException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PasswordException(final String message) {
        super(message);
    }

    public PasswordException(final Throwable cause) {
        super(cause);
    }
}
