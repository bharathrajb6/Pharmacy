package com.example.Pharmacy.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicationResponse {
    private int medicationID;
    private String name;
    private String description;
    private int price;
}
