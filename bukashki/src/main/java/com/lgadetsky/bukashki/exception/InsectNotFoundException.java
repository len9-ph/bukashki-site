package com.lgadetsky.bukashki.exception;

public class InsectNotFoundException extends RuntimeException {
    public InsectNotFoundException(Long insectId) {
        super("Insect not found for insectId: " + insectId);
    }

}
