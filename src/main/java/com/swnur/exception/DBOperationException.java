package com.swnur.exception;

public class DBOperationException extends RuntimeException {
    public DBOperationException(String message) {
        super(message);
    }
}
