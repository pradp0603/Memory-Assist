package com.demo.alzheimerassist.exception;

public class MissingContactNameException extends RuntimeException {

    public MissingContactNameException(String message) {
        super(message);
    }
}