package com.example.Pharmacy.service;

import com.example.Pharmacy.dtos.responses.PrescriptionResponse;
import com.example.Pharmacy.model.Prescription;

public interface PrescriptionService {

    PrescriptionResponse uploadPrescription(String comments, String type, byte[] imageData);

    PrescriptionResponse getPrescriptionDetails(String prescriptionID);

    Prescription getPrescriptionImage(String prescriptionID);
}
