package com.example.Pharmacy.controller;

import com.example.Pharmacy.dtos.request.BatchRequest;
import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.dtos.responses.BatchResponse;
import com.example.Pharmacy.dtos.responses.MedicationResponse;
import com.example.Pharmacy.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class MedicationController {

    private final MedicationService medicationService;

    /**
     * This method is used to add a new medication to the database
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/medication", method = RequestMethod.POST)
    public MedicationResponse addMedication(@RequestBody MedicationRequest request) {
        return medicationService.addMedication(request);
    }

    /**
     * This method is used to get the details of a medication
     *
     * @param medicationID
     * @return
     */
    @RequestMapping(value = "/medication/{medicationID}", method = RequestMethod.GET)
    public MedicationResponse getMedication(@PathVariable int medicationID) {
        return medicationService.getMedication(medicationID);
    }

    /**
     * This method is used to update the details of a medication
     *
     * @param medicationID
     * @param request
     * @return
     */
    @RequestMapping(value = "/medication/{medicationID}", method = RequestMethod.PUT)
    public MedicationResponse updateMedication(@PathVariable int medicationID, @RequestBody MedicationRequest request) {
        return medicationService.updateMedication(medicationID, request);
    }

    /**
     * This method is used to delete a medication from the database
     *
     * @param medicationID
     */
    @RequestMapping(value = "/medication/{medicationID}", method = RequestMethod.DELETE)
    public void deleteMedication(@PathVariable int medicationID) {
        medicationService.deleteMedication(medicationID);
    }

    /**
     * This method is used to get all the medications in the database
     *
     * @param pageable
     * @return
     */
    @RequestMapping(value = "/medication", method = RequestMethod.GET)
    public Page<MedicationResponse> getAllMedication(Pageable pageable) {
        return medicationService.getAllMedications(pageable);
    }

    /**
     * This method is used to add a new batch of a medication
     *
     * @param batchRequest
     * @param medicationID
     * @return
     */
    @RequestMapping(value = "/batch/{medicationID}", method = RequestMethod.POST)
    public BatchResponse addBatch(@RequestBody BatchRequest batchRequest, @PathVariable int medicationID) {
        return medicationService.addBatch(batchRequest, medicationID);
    }

    /**
     * This method is used to get the details of a batch
     *
     * @param batchNumber
     * @return
     */
    @RequestMapping(value = "/batch/{batchNumber}", method = RequestMethod.GET)
    public BatchResponse getBatch(@PathVariable String batchNumber) {
        return medicationService.getBatchDetails(batchNumber);
    }

    /**
     * This method is used to update the details of a batch
     *
     * @param medicationID
     * @return
     */
    @RequestMapping(value = "/batches/{medicationID}", method = RequestMethod.GET)
    public List<BatchResponse> getAllBatches(@PathVariable int medicationID) {
        return medicationService.getAllBatchesForMedication(medicationID);
    }

}
