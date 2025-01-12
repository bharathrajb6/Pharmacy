package com.example.Pharmacy.controller;

import com.example.Pharmacy.dtos.responses.PrescriptionResponse;
import com.example.Pharmacy.exception.PrescriptionException;
import com.example.Pharmacy.model.Prescription;
import com.example.Pharmacy.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/prescription")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    /**
     * This method is used to upload the prescription
     *
     * @param comments
     * @param image
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public PrescriptionResponse uploadPrescription(@RequestParam("comments") String comments, @RequestParam("image") MultipartFile image) {

        try {
            // Check if the image is empty
            if (image.isEmpty()) {
                throw new PrescriptionException("Image is empty");
            }

            // Check if the file is an image
            String contentType = image.getContentType();
            if (!contentType.startsWith("image/")) {
                throw new PrescriptionException("File is not an image");
            }

            // Get the bytes of the image
            byte[] imageData = image.getBytes();

            // Upload the prescription
            return prescriptionService.uploadPrescription(comments, contentType, imageData);
        } catch (IOException e) {
            throw new PrescriptionException("Unable to get the bytes");
        }
    }


    /**
     * This method is used to get the prescription details except uploaded image
     *
     * @param prescriptionID
     * @return
     */
    @RequestMapping(value = "/getDetails/{prescriptionID}", method = RequestMethod.GET)
    public PrescriptionResponse getPrescriptionDetails(@PathVariable String prescriptionID) {
        return prescriptionService.getPrescriptionDetails(prescriptionID);
    }

    /**
     * This method is used to get the prescription image
     *
     * @param prescriptionID
     * @return
     */
    @RequestMapping(value = "/getImage/{prescriptionID}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getPrescriptionImage(@PathVariable String prescriptionID) {
        Prescription prescription = prescriptionService.getPrescriptionImage(prescriptionID);
        return ResponseEntity.ok().header("Content-Type", prescription.getFileType()).body(prescription.getUploadedFile());
    }
}
