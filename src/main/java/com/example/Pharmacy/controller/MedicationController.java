package com.example.Pharmacy.controller;

import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.dtos.responses.MedicationResponse;
import com.example.Pharmacy.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class MedicationController {

    private final MedicationService medicationService;

    @RequestMapping(value = "/medication", method = RequestMethod.POST)
    public MedicationResponse addMedication(@RequestBody MedicationRequest request) {
        return medicationService.addMedication(request);
    }

    @RequestMapping(value = "/medication/{medicationID}", method = RequestMethod.GET)
    public MedicationResponse getMedication(@PathVariable int medicationID) {
        return medicationService.getMedication(medicationID);
    }

    @RequestMapping(value = "/medication/{medicationID}", method = RequestMethod.PUT)
    public MedicationResponse updateMedication(@PathVariable int medicationID, @RequestBody MedicationRequest request) {
        return medicationService.updateMedication(medicationID, request);
    }

    @RequestMapping(value = "/medication/{medicationID}", method = RequestMethod.DELETE)
    public void deleteMedication(@PathVariable int medicationID) {
        medicationService.deleteMedication(medicationID);
    }

    @RequestMapping(value = "/medication", method = RequestMethod.GET)
    public Page<MedicationResponse> getAllMedication(Pageable pageable) {
        return medicationService.getAllMedications(pageable);
    }
}
