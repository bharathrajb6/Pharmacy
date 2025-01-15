package com.example.Pharmacy.validators;

import com.example.Pharmacy.dtos.request.MedicationOrderRequest;
import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.exception.OrderException;

public class OrderValidator {

    /**
     * Validates the order request
     *
     * @param request
     */
    public static void validateOrderRequest(OrderRequest request) {

        // Check if the request is null
        if (request == null) {
            throw new OrderException("Order request cannot be null");
        }

        // Check if the username is null or empty or blank
        if (request.getUsername() == null || request.getUsername().isBlank() || request.getUsername().isEmpty()) {
            throw new OrderException("Username cannot be null");
        }

        // Check if the medication order request list is null
        if (request.getMedicationOrderRequestList() == null) {
            throw new OrderException("Medication order request list cannot be null");
        }

        for (MedicationOrderRequest medicationOrderRequest : request.getMedicationOrderRequestList()) {

            // Check if the batch number is null or empty or blank
            if (medicationOrderRequest.getBatchNumber() == null || medicationOrderRequest.getBatchNumber().isEmpty() || medicationOrderRequest.getBatchNumber().isBlank()) {
                throw new OrderException("Batch number cannot be null");
            }

            // Check if the quantity is less than or equal to zero
            if (medicationOrderRequest.getQuantity() <= 0) {
                throw new OrderException("Quantity cannot be zero");
            }
        }
    }
}
