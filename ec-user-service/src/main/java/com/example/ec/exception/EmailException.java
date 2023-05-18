package com.example.ec.exception;

public class EmailException extends Exception {
    private static final long serialVersionUID = 1L;

    public EmailException(String message) {
        super(message);
    }
}
