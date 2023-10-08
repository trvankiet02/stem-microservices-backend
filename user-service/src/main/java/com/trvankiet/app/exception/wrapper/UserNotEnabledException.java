package com.trvankiet.app.exception.wrapper;

public class UserNotEnabledException extends RuntimeException{
    public UserNotEnabledException() {
        super();
    }

    public UserNotEnabledException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserNotEnabledException(final String message) {
        super(message);
    }

    public UserNotEnabledException(final Throwable cause) {
        super(cause);
    }
}
