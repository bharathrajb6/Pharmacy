package com.example.Pharmacy.helper;

import com.example.Pharmacy.dtos.request.MedicationOrderRequest;
import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.dtos.responses.BatchResponse;
import com.example.Pharmacy.dtos.responses.MedicationOrderResponse;
import com.example.Pharmacy.exception.OrderException;
import com.example.Pharmacy.model.Medication;
import com.example.Pharmacy.model.MedicationQuantity;
import com.example.Pharmacy.model.OrderStatus;
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

    /**
     * This method is used to generate the order
     *
     * @param request
     * @return
     */
    public Orders generateOrder(OrderRequest request) {
        for (MedicationOrderRequest medicationOrderRequest : request.getMedicationOrderRequestList()) {
            checkIfMedicationStockIsThere(medicationOrderRequest.getBatchNumber(), medicationOrderRequest.getQuantity());
        }
        Orders order = new Orders();
        order.setOrderID("O" + generateID());
        order.setUsername(request.getUsername());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderedDate(LocalDate.now());
        order.setTotalAmount(calculateTotalAmount(request.getMedicationOrderRequestList()));
        return order;
    }

    /**
     * This method is used to generate the medication quantity model
     *
     * @param medicationOrderRequestList
     * @param order
     * @return
     */
    public List<MedicationQuantity> generateMedicationQuantity(List<MedicationOrderRequest> medicationOrderRequestList, Orders order) {
        return medicationOrderRequestList.stream().map(medicationOrderRequest -> {
            return new MedicationQuantity("MQ" + generateID(), medicationOrderRequest.getBatchNumber(), medicationOrderRequest.getQuantity(), order);
        }).toList();
    }


    /**
     * This method is used to check if the stock is there or not
     *
     * @param batchNumber
     * @param quantity
     */
    private void checkIfMedicationStockIsThere(String batchNumber, int quantity) {
        BatchResponse batchResponse = medicationService.getBatchDetails(batchNumber);
        if (quantity > batchResponse.getQuantity()) {
            throw new OrderException("Stock is not there. Please select other batch.");
        }
    }

    /**
     * This method is used to get the medication details
     *
     * @param batchNumber
     * @return
     */
    private Medication getMedicationDetails(String batchNumber) {
        return medicationService.getMedicationDetailsByBatch(batchNumber);
    }

    /**
     * This method is used to calculate the total amount of all the medications in the order
     *
     * @param medicationOrderRequestList
     * @return
     */
    private double calculateTotalAmount(List<MedicationOrderRequest> medicationOrderRequestList) {
        return medicationOrderRequestList.stream().mapToDouble(medicationOrderRequest -> getMedicationDetails(medicationOrderRequest.getBatchNumber()).getPrice() * medicationOrderRequest.getQuantity()).sum();
    }

    /**
     * This method is used to get the medication order response
     *
     * @param medicationQuantities
     * @return
     */
    public List<MedicationOrderResponse> getMedicationOrderResponse(List<MedicationQuantity> medicationQuantities) {
        return medicationQuantities.stream().map(medicationQuantity -> {
            Medication medication = getMedicationDetails(medicationQuantity.getBatchNumber());
            return new MedicationOrderResponse(medication.getMedicationID(), medication.getName(), medication.getPrice(), medicationQuantity.getBatchNumber(), medicationQuantity.getQuantity());
        }).toList();
    }

    /**
     * This method is used to update the medication stock after the order is confirmed or cancelled
     *
     * @param medicationOrderRequestList
     * @param isOrderConfirmed
     */
    public void updateMedicationStock(List<MedicationQuantity> medicationOrderRequestList, boolean isOrderConfirmed) {
        medicationOrderRequestList.forEach(medicationQuantity -> medicationService.updateMedicationBatchStock(medicationQuantity.getBatchNumber(), medicationQuantity.getQuantity(), isOrderConfirmed));
    }
}
