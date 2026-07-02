package com.lgadetsky.bukashki.exception;

public class PhotoNotFoundException extends RuntimeException {
    public PhotoNotFoundException(Long id) {
        super("Photo not found for id: " + id);
    }

}
