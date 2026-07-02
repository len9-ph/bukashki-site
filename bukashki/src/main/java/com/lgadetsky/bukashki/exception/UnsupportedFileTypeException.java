package com.lgadetsky.bukashki.exception;

public class UnsupportedFileTypeException extends RuntimeException {
    public UnsupportedFileTypeException(String contentType) {
        super("Unsupported file type: " + contentType);
    }
}
