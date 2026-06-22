package com.lgadetsky.bukashki.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("User not found for userId: " + userId);
    }

}
