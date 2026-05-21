package com.peoplecore.exception;



public class UserSoftDeleteException extends RuntimeException {

    public UserSoftDeleteException(String message) {
        super(message);
    }
}
