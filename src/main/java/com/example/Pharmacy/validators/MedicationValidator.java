package com.example.Pharmacy.validators;

import com.example.Pharmacy.dtos.request.BatchRequest;
import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.exception.BatchException;
import com.example.Pharmacy.exception.MedicationException;

import java.time.LocalDate;


public class MedicationValidator {

    /**
     * Validate medication request
     *
     * @param medicationRequest
     */
    public static void validateMedication(MedicationRequest medicationRequest) {

        if (medicationRequest.getName() == null || medicationRequest.getName().isBlank() || medicationRequest.getName().isEmpty()) {
            throw new MedicationException("Medication name cannot be null or empty");
        }

        if (medicationRequest.getName().length() < 5) {
            throw new MedicationException("Medication Name length is too short");
        }

        if (medicationRequest.getName().length() > 255) {
            throw new MedicationException("Medication Name length is too long");
        }

        if (medicationRequest.getDescription() == null || medicationRequest.getDescription().isEmpty() || medicationRequest.getDescription().isBlank()) {
            throw new MedicationException("Description cannot be null or empty");
        }

        if (medicationRequest.getDescription().length() < 5) {
            throw new MedicationException("Description length is too short");
        }

        if (medicationRequest.getDescription().length() > 255) {
            throw new MedicationException("Description length is too long");
        }

        if (medicationRequest.getPrice() <= 0) {
            throw new MedicationException("Invalid price for medication");
        }

    }

    /**
     * Validate batch details
     *
     * @param batchRequest
     */
    public static void validateBatchDetails(BatchRequest batchRequest) {

        if (batchRequest.getBatchNumber() == null || batchRequest.getBatchNumber().isBlank() || batchRequest.getBatchNumber().isEmpty()) {
            throw new BatchException("Batch number cannot be null or empty");
        }

        if (batchRequest.getQuantity() <= 0) {
            throw new BatchException("Invalid quantity for batch");
        }

        if (batchRequest.getManufactureDate() == null) {
            throw new BatchException("Manufacture date cannot be null");
        }

        if (batchRequest.getManufactureDate().isAfter(LocalDate.now())) {
            throw new BatchException("Manufacture date cannot be after today");
        }

        if (batchRequest.getExpiryDate() == null) {
            throw new BatchException("Expiry date cannot be null");
        }

        if (batchRequest.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BatchException("Expiry date cannot be before today");
        }
    }
}
