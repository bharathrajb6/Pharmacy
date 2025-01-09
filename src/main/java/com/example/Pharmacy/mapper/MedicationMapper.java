package com.example.Pharmacy.mapper;

import com.example.Pharmacy.dtos.request.MedicationRequest;
import com.example.Pharmacy.dtos.responses.MedicationResponse;
import com.example.Pharmacy.model.Medication;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MedicationMapper {

    Medication toMedication(MedicationRequest request);

    MedicationResponse toMedicationResponse(Medication medication);

    default Page<MedicationResponse> toMedicationResponse(Page<Medication> medications) {
        List<MedicationResponse> responses = medications.getContent()
                .stream()
                .map(this::toMedicationResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, medications.getPageable(), medications.getTotalElements());
    }
}
