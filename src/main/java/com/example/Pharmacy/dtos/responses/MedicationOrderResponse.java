package com.example.Pharmacy.dtos.responses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicationOrderResponse {
    private int medicationID;
    private String medicationName;
    private double price;
    private String batchNumber;
    private int quantity;
}
