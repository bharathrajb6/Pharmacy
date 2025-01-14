package com.example.Pharmacy.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String orderID;
    private List<MedicationOrderResponse> medications;
    private String username;
    private double totalAmount;
    private String orderStatus;
    private LocalDate orderedDate;
}
