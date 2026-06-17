package com.peoplecore.exception;

public class UnauthorizedResourceAccessException extends RuntimeException {

    public UnauthorizedResourceAccessException(String message) {
        super(message);
    }

    public UnauthorizedResourceAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}