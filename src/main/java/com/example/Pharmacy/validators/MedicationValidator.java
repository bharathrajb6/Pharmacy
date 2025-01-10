package com.example.Pharmacy.validators;

import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.exception.MedicationException;


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
}
