package com.peoplecore.exception;

public class DocumentAlreadyRejectedException extends RuntimeException {

    public DocumentAlreadyRejectedException(String message) {
        super(message);
    }
}
