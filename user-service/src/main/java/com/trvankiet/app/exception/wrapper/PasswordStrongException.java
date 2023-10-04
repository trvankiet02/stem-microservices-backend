package com.trvankiet.app.exception.wrapper;

public class PasswordStrongException extends RuntimeException {

    public PasswordStrongException() {
        super();
    }

    public PasswordStrongException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PasswordStrongException(final String message) {
        super(message);
    }

    public PasswordStrongException(final Throwable cause) {
        super(cause);
    }
}
