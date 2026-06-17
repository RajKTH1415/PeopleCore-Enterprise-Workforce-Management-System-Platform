package com.peoplecore.exception;


public class InvalidDocumentStatusException extends RuntimeException {

    public InvalidDocumentStatusException(String message) {
        super(message);
    }
}
