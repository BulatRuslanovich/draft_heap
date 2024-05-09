package com.bul.exaptions;

public class UnknownUserException extends RuntimeException {
    public UnknownUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownUserException(String message) {
        super(message);
    }

    public UnknownUserException(Throwable cause) {
        super(cause);
    }
}
