package com.example.Pharmacy.exception;

public class BatchException extends RuntimeException {
    public BatchException(String message) {
        super(message);
    }

    public BatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
