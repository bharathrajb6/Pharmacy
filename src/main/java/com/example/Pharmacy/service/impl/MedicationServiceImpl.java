package com.example.Pharmacy.service.impl;

import com.example.Pharmacy.dtos.request.BatchRequest;
import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.dtos.responses.BatchResponse;
import com.example.Pharmacy.dtos.responses.MedicationResponse;
import com.example.Pharmacy.exception.BatchException;
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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.Pharmacy.messages.Batch.BatchExceptionMessages.BATCH_NOT_FOUND;
import static com.example.Pharmacy.messages.Batch.BatchLogMessages.*;
import static com.example.Pharmacy.messages.Medication.MedicationExceptionMessages.UNABLE_FETCH_MEDICATION;
import static com.example.Pharmacy.messages.Medication.MedicationLogMessages.*;
import static com.example.Pharmacy.utils.CommonUtils.generateID;
import static com.example.Pharmacy.validators.MedicationValidator.validateBatchDetails;
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

    /**
     * Add the medication details to database
     *
     * @param medicationRequest
     * @return
     */
    @Override
    public MedicationResponse addMedication(MedicationRequest medicationRequest) {
        // Validate the medication details
        validateMedication(medicationRequest);
        // Map the medication request to medication
        Medication medication = medicationMapper.toMedication(medicationRequest);
        medication.setMedicationID(generateID());
        try {
            // Save the medication details to database
            medicationRepository.save(medication);
            log.info(LOG_MEDICATION_SAVED_SUCCESSFULLY);
            return medicationMapper.toMedicationResponse(medication);
        } catch (Exception exception) {
            // Log the error message
            log.error(String.format(LOG_UNABLE_SAVE_MEDICATION, exception.getMessage()));
            throw new MedicationException(exception.getMessage());
        }
    }

    /**
     * Fetch the medication details from database based on medicationID
     *
     * @param medicationID
     * @return
     */
    @Override
    public MedicationResponse getMedication(int medicationID) {
        // Fetch the medication details from database based on medicationID
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            log.error(String.format(LOG_MEDICATION_NOT_FOUND, medicationID));
            return new MedicationException(UNABLE_FETCH_MEDICATION);
        });

        // Map the medication to medication response
        return medicationMapper.toMedicationResponse(medication);
    }

    /**
     * Update the medication details in database based on medicationID
     *
     * @param medicationID
     * @param request
     * @return
     */
    @Override
    public MedicationResponse updateMedication(int medicationID, MedicationRequest request) {
        // Validate the medication details
        validateMedication(request);
        // Fetch the medication details from database based on medicationID
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            log.error(String.format(LOG_MEDICATION_NOT_FOUND, medicationID));
            return new MedicationException(UNABLE_FETCH_MEDICATION);
        });

        try {
            medicationRepository.updateMedicationDetails(request.getName(), request.getDescription(), medication.getPrice(), medicationID);
            log.info(LOG_MEDICATION_UPDATED_SUCCESSFULLY);
            return getMedication(medicationID);
        } catch (Exception exception) {
            log.error(String.format(LOG_UNABLE_UPDATE_MEDICATION, exception.getMessage()));
            throw new MedicationException(exception.getMessage());
        }
    }

    /**
     * Delete the medication details from database based on medicationID
     *
     * @param medicationID
     */
    @Override
    public void deleteMedication(int medicationID) {
        // Fetch the medication details from database based on medicationID
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            log.error(String.format(LOG_MEDICATION_NOT_FOUND, medicationID));
            return new MedicationException(UNABLE_FETCH_MEDICATION);
        });

        try {
            // Delete the medication details from database
            medicationRepository.delete(medication);
            log.info(LOG_MEDICATION_DELETED_SUCCESSFULLY);
        } catch (Exception exception) {
            log.error(String.format(LOG_UNABLE_DELETE_MEDICATION, exception.getMessage()));
            throw new MedicationException(exception.getMessage());
        }
    }

    /**
     * Fetch all the medications from database
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<MedicationResponse> getAllMedications(Pageable pageable) {
        // Fetch all the medications from database
        Page<Medication> medications = medicationRepository.findAll(pageable);
        return medicationMapper.toMedicationResponse(medications);
    }

    /**
     * Add the batch details to database
     *
     * @param batchRequest
     * @param medicationID
     * @return
     */
    @Override
    public BatchResponse addBatch(BatchRequest batchRequest, int medicationID) {
        // Validate the batch details
        validateBatchDetails(batchRequest);
        // Map the batch request to batch
        Batch batch = batchMapper.toBatch(batchRequest);
        // Fetch the medication details from database based on medicationID
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            log.error(String.format(LOG_MEDICATION_NOT_FOUND, medicationID));
            return new MedicationException(UNABLE_FETCH_MEDICATION);
        });

        // Set the medication details to batch
        batch.setMedication(medication);
        try {
            // Save the batch details to database
            batchRepository.save(batch);
            log.info(LOG_BATCH_SAVED_SUCCESSFULLY);
            return batchMapper.toBatchResponse(batch);
        } catch (Exception exception) {
            // Log the error message
            log.error(String.format(LOG_UNABLE_SAVE_BATCH, exception.getMessage()));
            throw new BatchException(exception.getMessage());
        }
    }

    /**
     * Fetch the batch details from database based on batchNumber
     *
     * @param batchNumber
     * @return
     */
    @Override
    public BatchResponse getBatchDetails(String batchNumber) {
        // Fetch the batch details from database based on batchNumber
        Batch batch = batchRepository.findByBatchNumber(batchNumber).orElseThrow(() -> {
            log.info(LOG_BATCH_NOT_FOUND);
            return new BatchException(BATCH_NOT_FOUND);
        });
        // Map the batch to batch response
        return batchMapper.toBatchResponse(batch);
    }

    /**
     * Get all batches for the medication based on medicationID
     *
     * @param medicationID
     * @return
     */
    @Override
    public List<BatchResponse> getAllBatchesForMedication(int medicationID) {
        // Fetch the medication details from database based on medicationID
        Medication medication = medicationRepository.findById(medicationID).orElseThrow(() -> {
            log.error(String.format(LOG_MEDICATION_NOT_FOUND, medicationID));
            return new MedicationException(UNABLE_FETCH_MEDICATION);
        });

        // Fetch all the batches for the medication
        List<Batch> batches = batchRepository.getAllBatchesForMedication(medication);
        return batchMapper.toBatchResponseList(batches);
    }

    /**
     * Get all the batches based on expiry date
     *
     * @param date
     * @return
     */
    @Override
    public List<BatchResponse> getBatchesByExpiryDate(String date) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new BatchException(exception.getMessage());
        }
        // Fetch all the batches based on expiry date
        List<Batch> batches = batchRepository.getBatchesByExpiryDate(localDate);
        return batchMapper.toBatchResponseList(batches);
    }

    /**
     * Check the stock for all the batches
     *
     * @return
     */
    private List<BatchResponse> checkBatchStock() {
        // Fetch all the batches
        List<Batch> batches = batchRepository.findAll();

        // Filter the batches which stock is less than 10
        return batches.stream().map(batchMapper::toBatchResponse).filter(batchResponse -> batchResponse.getQuantity() < 10).toList();
    }

    /**
     * Send email to user if the stock is less than 10
     */
    @Scheduled(cron = "0 0 9 1/1 * ?")
    public void batchStockAlert() {
        // Get the email of the user
        String email = userService.getUserDetails().getEmail();
        // Check the stock for all the batches
        List<BatchResponse> batchResponses = checkBatchStock();
        // If the stock is less than 10, send email to user
        String body = batchResponses.stream().
                map(batchResponse -> String.format("ID - %s, Stock - %d",
                        batchResponse.getBatchNumber(), batchResponse.getQuantity())).
                collect(Collectors.joining("\n"));
        // Send email to user
        emailService.sendEmail(email, "Stock Update", body);
    }

    @Override
    public Medication getMedicationDetailsByBatch(String batchNumber) {
        Batch batch = batchRepository.findByBatchNumber(batchNumber).orElseThrow(() -> {
            log.error(LOG_BATCH_NOT_FOUND);
            return new BatchException(BATCH_NOT_FOUND);
        });
        return batch.getMedication();
    }
}
