package com.example.Pharmacy.mapper;

import com.example.Pharmacy.dtos.request.MedicationOrderRequest;
import com.example.Pharmacy.dtos.responses.MedicationOrderResponse;
import com.example.Pharmacy.model.MedicationQuantity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationOrderMapper {

    MedicationQuantity toMedicationQuantity(MedicationOrderRequest medicationOrderRequest);

    MedicationOrderResponse toMedicationOrderResponse(MedicationQuantity medicationQuantity);
}
