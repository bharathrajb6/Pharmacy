package com.example.Pharmacy.validators;

import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.exception.MedicationException;

import java.time.Instant;
import java.util.Date;

public class MedicationValidator {

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

        if (medicationRequest.getManufacturer() == null || medicationRequest.getManufacturer().isEmpty() || medicationRequest.getManufacturer().isBlank()) {
            throw new MedicationException("Manufacturer cannot be null or empty");
        }

        if (medicationRequest.getManufacturer().length() < 5) {
            throw new MedicationException("Manufacturer length is too short");
        }

        if (medicationRequest.getManufacturer().length() > 255) {
            throw new MedicationException("Manufacturer length is too long");
        }

        if (medicationRequest.getPrice() <= 0) {
            throw new MedicationException("Invalid price for medication");
        }

        if (medicationRequest.getStockQuantity() < -1) {
            throw new MedicationException("Invalid stock quantity");
        }

        if (medicationRequest.getExpiryDate() == null || medicationRequest.getExpiryDate().before(Date.from(Instant.now()))) {
            throw new MedicationException("Invalid expiry date");
        }

        if (medicationRequest.getManufacturedDate() == null || medicationRequest.getManufacturedDate().after(Date.from(Instant.now()))) {
            throw new MedicationException("Invalid manufactured date");
        }
    }
}
