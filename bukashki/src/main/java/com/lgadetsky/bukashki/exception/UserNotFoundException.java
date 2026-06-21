package com.lgadetsky.bukashki.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String login) {
        super("User not found for login: " + login);
    }

}
