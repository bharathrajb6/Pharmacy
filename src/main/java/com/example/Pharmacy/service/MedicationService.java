package com.example.Pharmacy.service;

import com.example.Pharmacy.dtos.request.BatchRequest;
import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.dtos.responses.BatchResponse;
import com.example.Pharmacy.dtos.responses.MedicationResponse;
import com.example.Pharmacy.model.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicationService {

    MedicationResponse addMedication(MedicationRequest medicationRequest);

    MedicationResponse getMedication(int medicationID);

    MedicationResponse updateMedication(int medicationID, MedicationRequest medicationRequest);

    void deleteMedication(int medicationID);

    Page<MedicationResponse> getAllMedications(Pageable pageable);

    BatchResponse addBatch(BatchRequest batchRequest, int medicationID);

    BatchResponse getBatchDetails(String batchNumber);

    List<BatchResponse> getAllBatchesForMedication(int medicationID);

    List<BatchResponse> getBatchesByExpiryDate(String date);

    Medication getMedicationDetailsByBatch(String batchNumber);
}
