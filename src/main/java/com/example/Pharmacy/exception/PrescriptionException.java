package com.example.Pharmacy.exception;

public class PrescriptionException extends RuntimeException {

    public PrescriptionException(String message) {
        super(message);
    }

    public PrescriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
