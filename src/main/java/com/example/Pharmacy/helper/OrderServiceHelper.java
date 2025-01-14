package com.example.Pharmacy.helper;

import com.example.Pharmacy.dtos.request.MedicationOrderRequest;
import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.dtos.responses.BatchResponse;
import com.example.Pharmacy.dtos.responses.MedicationOrderResponse;
import com.example.Pharmacy.exception.OrderException;
import com.example.Pharmacy.model.Medication;
import com.example.Pharmacy.model.MedicationQuantity;
import com.example.Pharmacy.model.Orders;
import com.example.Pharmacy.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.example.Pharmacy.utils.CommonUtils.generateID;

@Component
@RequiredArgsConstructor
public class OrderServiceHelper {

    private final MedicationService medicationService;

    public Orders generateOrder(OrderRequest request) {
        for (MedicationOrderRequest medicationOrderRequest : request.getMedicationOrderRequestList()) {
            checkIfMedicationStockIsThere(medicationOrderRequest.getBatchNumber(), medicationOrderRequest.getQuantity());
        }
        Orders order = new Orders();
        order.setOrderID("O" + generateID());
        order.setUsername(request.getUsername());
        order.setOrderStatus("Pending");
        order.setOrderedDate(LocalDate.now());
        order.setTotalAmount(calculateTotalAmount(request.getMedicationOrderRequestList()));
        return order;
    }

    public List<MedicationQuantity> generateMedicationQuantity(List<MedicationOrderRequest> medicationOrderRequestList, Orders order) {
        return medicationOrderRequestList.stream().map(medicationOrderRequest -> {
            return new MedicationQuantity("MQ" + generateID(), medicationOrderRequest.getBatchNumber(), medicationOrderRequest.getQuantity(), order);
        }).toList();
    }


    private void checkIfMedicationStockIsThere(String batchNumber, int quantity) {
        BatchResponse batchResponse = medicationService.getBatchDetails(batchNumber);
        if (quantity > batchResponse.getQuantity()) {
            throw new OrderException("Stock is not there. Please select other batch.");
        }
    }

    private Medication getMedicationDetails(String batchNumber) {
        return medicationService.getMedicationDetailsByBatch(batchNumber);
    }

    private double calculateTotalAmount(List<MedicationOrderRequest> medicationOrderRequestList) {
        return medicationOrderRequestList.stream().mapToDouble(medicationOrderRequest -> getMedicationDetails(medicationOrderRequest.getBatchNumber()).getPrice() * medicationOrderRequest.getQuantity()).sum();
    }

    public List<MedicationOrderResponse> getMedicationOrderResponse(List<MedicationQuantity> medicationQuantities) {
        return medicationQuantities.stream().map(medicationQuantity -> {
            Medication medication = getMedicationDetails(medicationQuantity.getBatchNumber());
            return new MedicationOrderResponse(medication.getMedicationID(), medication.getName(), medication.getPrice(), medicationQuantity.getBatchNumber(), medicationQuantity.getQuantity());
        }).toList();
    }

    public void updateMedicationStock(List<MedicationOrderRequest> medicationOrderRequestList) {
        for (MedicationOrderRequest medicationOrderRequest : medicationOrderRequestList) {
            medicationService.updateBatchStock(medicationOrderRequest.getBatchNumber(), medicationOrderRequest.getQuantity());
        }
    }
}
