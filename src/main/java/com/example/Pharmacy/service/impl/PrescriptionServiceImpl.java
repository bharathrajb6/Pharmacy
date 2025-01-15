package com.example.Pharmacy.service.impl;

import com.example.Pharmacy.dtos.responses.PrescriptionResponse;
import com.example.Pharmacy.exception.PrescriptionException;
import com.example.Pharmacy.model.Prescription;
import com.example.Pharmacy.repo.PrescriptionRepository;
import com.example.Pharmacy.service.PrescriptionService;
import com.example.Pharmacy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.Pharmacy.utils.CommonUtils.generateID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final UserService userService;

    /**
     * This method is used to upload the prescription
     *
     * @param comments
     * @param type
     * @param imageData
     * @return
     */
    @Transactional
    @Override
    public PrescriptionResponse uploadPrescription(String comments, String type, byte[] imageData) {
        String username = userService.getUserDetails().getUsername();

        Prescription prescription = new Prescription();
        String id = "P" + generateID();
        prescription.setPrescriptionId(id);
        prescription.setStatus("Pending");
        prescription.setUsername(username);
        prescription.setComments(comments);
        prescription.setUploadedFile(imageData);
        prescription.setFileType(type);

        try {
            prescriptionRepository.save(prescription);
        } catch (Exception exception) {
            throw new PrescriptionException(exception.getMessage());
        }

        return PrescriptionResponse.builder().id(id).username(username).status("Pending").comments(comments).build();
    }

    /**
     * This method will return prescription details except the image
     *
     * @param prescriptionID
     * @return
     */
    @Override
    public PrescriptionResponse getPrescriptionDetails(String prescriptionID) {
        Prescription prescription = prescriptionRepository.findById(prescriptionID).orElseThrow(() -> {
            return new PrescriptionException("Prescription not found with this ID");
        });

        return PrescriptionResponse.builder().id(prescriptionID).
                username(prescription.getUsername()).status(prescription.getStatus()).comments(prescription.getComments()).build();
    }

    /**
     * This method is used to get the prescription image
     *
     * @param prescriptionID
     * @return
     */
    @Override
    public Prescription getPrescriptionImage(String prescriptionID) {
        return prescriptionRepository.findById(prescriptionID).orElseThrow(() -> {
            return new PrescriptionException("Prescription not found with this ID");
        });
    }
}
