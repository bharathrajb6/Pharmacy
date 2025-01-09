package com.example.Pharmacy.service.impl;

import com.example.Pharmacy.dtos.request.BatchRequest;
import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.dtos.responses.BatchResponse;
import com.example.Pharmacy.dtos.responses.MedicationResponse;
import com.example.Pharmacy.exception.MedicationException;
import com.example.Pharmacy.mapper.BatchMapper;
import com.example.Pharmacy.mapper.MedicationMapper;
import com.example.Pharmacy.model.Batch;
import com.example.Pharmacy.model.Medication;
import com.example.Pharmacy.repo.BatchRepository;
import com.example.Pharmacy.repo.MedicationRepository;
import com.example.Pharmacy.service.EmailService;
import com.example.Pharmacy.service.MedicationService;
import com.example.Pharmacy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.Pharmacy.utils.CommonUtils.generateID;
import static com.example.Pharmacy.validators.MedicationValidator.validateMedication;

@Service
@Slf4j
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationMapper medicationMapper;
    private final MedicationRepository medicationRepository;
    private final BatchRepository batchRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final BatchMapper batchMapper;

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
            medicationRepository.updateMedicationDetails(request.getName(), request.getDescription(), medication.getPrice(), medicationID);
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

    @Override
    public BatchResponse addBatch(BatchRequest batchRequest, int medicationID) {
        Batch batch = batchMapper.toBatch(batchRequest);
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            return new MedicationException("Medication not found");
        });

        batch.setMedication(medication);
        try {
            batchRepository.save(batch);
            return batchMapper.toBatchResponse(batch);
        } catch (Exception exception) {
            throw new MedicationException(exception.getMessage());
        }
    }

    @Override
    public BatchResponse getBatchDetails(String batchNumber) {
        Batch batch = batchRepository.findByBatchNumber(batchNumber).orElseThrow(() -> {
            return new MedicationException("");
        });
        return batchMapper.toBatchResponse(batch);
    }

    @Override
    public List<BatchResponse> getAllBatchesForMedication(int medicationID) {
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            return new MedicationException("Medication not found");
        });
        List<Batch> batches = batchRepository.getAllBatchesForMedication(medication);
        return batchMapper.toBatchResponseList(batches);
    }


    private List<BatchResponse> checkBatchStock() {
        List<Batch> batches = batchRepository.findAll();
        return batches.stream().map(batchMapper::toBatchResponse).filter(batchResponse -> batchResponse.getQuantity() < 10).toList();
    }

    @Scheduled(cron = "0 0 9 1/1 * ?")
    public void batchStockAlert() {
        String email = userService.getUserDetails().getEmail();
        List<BatchResponse> batchResponses = checkBatchStock();
        String body = batchResponses.stream().
                map(batchResponse -> String.format("ID - %s, Stock - %d",
                        batchResponse.getBatchNumber(), batchResponse.getQuantity())).
                collect(Collectors.joining("\n"));
        emailService.sendEmail(email, "Stock Update", body);
    }
}
