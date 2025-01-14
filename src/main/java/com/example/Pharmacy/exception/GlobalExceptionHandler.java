package com.example.Pharmacy.exception;

import com.example.Pharmacy.dtos.responses.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * This method is used to handle user exceptions
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handleUserException(UserException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "User Error", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method is used to handle medication exceptions
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(MedicationException.class)
    public ResponseEntity<?> handleMedicationException(MedicationException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Medication Error", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method is used to handle batch exceptions
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(BatchException.class)
    public ResponseEntity<?> handleBatchException(BatchException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Batch Error", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method is used to handle prescription exceptions
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(PrescriptionException.class)
    public ResponseEntity<?> handlePrescription(PrescriptionException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Prescription Error", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method is used to handle order exceptions
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<?> handleOrderException(OrderException exception) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Order Error", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
