package com.peoplecore.exception;



public class DocumentBulkDeletionException extends RuntimeException {

    public DocumentBulkDeletionException(String message) {
        super(message);
    }

    public DocumentBulkDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}