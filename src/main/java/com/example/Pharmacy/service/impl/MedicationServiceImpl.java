package com.example.Pharmacy.service.impl;

import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.dtos.responses.MedicationResponse;
import com.example.Pharmacy.exception.MedicationException;
import com.example.Pharmacy.mapper.MedicationMapper;
import com.example.Pharmacy.model.Medication;
import com.example.Pharmacy.repo.MedicationRepository;
import com.example.Pharmacy.service.MedicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.example.Pharmacy.utils.CommonUtils.generateID;
import static com.example.Pharmacy.validators.MedicationValidator.validateMedication;

@Service
@Slf4j
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationMapper medicationMapper;
    private final MedicationRepository medicationRepository;

    @Override
    public MedicationResponse addMedication(MedicationRequest medicationRequest) {
        validateMedication(medicationRequest);
        Medication medication = medicationMapper.toMedication(medicationRequest);
        medication.setMedicationID(generateID());
        try {
            medicationRepository.save(medication);
            return medicationMapper.toMedicationResponse(medication);
        } catch (Exception exception) {
            throw new MedicationException(exception.getMessage());
        }
    }

    @Override
    public MedicationResponse getMedication(int medicationID) {
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            log.error("Unable to fetch the details" + medicationID);
            return new MedicationException("Unable to fetch the details");
        });

        return medicationMapper.toMedicationResponse(medication);
    }

    @Override
    public MedicationResponse updateMedication(int medicationID, MedicationRequest request) {
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            return new MedicationException("unable to fetch the details");
        });
        validateMedication(request);
        try {
            medicationRepository.updateMedicationDetails(request.getName(), request.getDescription(), medication.getManufacturer(), medication.getPrice(), medication.getStockQuantity(), medication.getManufacturedDate(), medication.getExpiryDate(), medicationID);
            return getMedication(medicationID);
        } catch (Exception exception) {
            throw new MedicationException(exception.getMessage());
        }
    }

    @Override
    public void deleteMedication(int medicationID) {
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            return new MedicationException("unable to fetch the details");
        });
        try {
            medicationRepository.delete(medication);
        } catch (Exception exception) {
            throw new MedicationException(exception.getMessage());
        }
    }

    @Override
    public Page<MedicationResponse> getAllMedications(Pageable pageable) {
        Page<Medication> medications = medicationRepository.findAll(pageable);
        return medicationMapper.toMedicationResponse(medications);
    }
}
