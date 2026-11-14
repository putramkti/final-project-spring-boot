package com.bootcamp19.auth_service.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String username) {
        super("Username '" + username + "' is already registered");
    }
}
