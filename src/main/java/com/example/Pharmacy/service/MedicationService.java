package com.example.Pharmacy.service;

import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.dtos.responses.MedicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicationService {

    MedicationResponse addMedication(MedicationRequest medicationRequest);

    MedicationResponse getMedication(int medicationID);

    MedicationResponse updateMedication(int medicationID, MedicationRequest medicationRequest);

    void deleteMedication(int medicationID);

    Page<MedicationResponse> getAllMedications(Pageable pageable);
}
