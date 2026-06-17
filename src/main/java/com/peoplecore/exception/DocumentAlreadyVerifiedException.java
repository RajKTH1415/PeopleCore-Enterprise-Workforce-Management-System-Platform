package com.peoplecore.exception;


public class DocumentAlreadyVerifiedException extends RuntimeException {

    public DocumentAlreadyVerifiedException(String message) {
        super(message);
    }
}
