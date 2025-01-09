package com.example.Pharmacy.mapper;

import com.example.Pharmacy.dtos.request.BatchRequest;
import com.example.Pharmacy.dtos.responses.BatchResponse;
import com.example.Pharmacy.model.Batch;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    Batch toBatch(BatchRequest request);

    BatchResponse toBatchResponse(Batch batch);

    List<BatchResponse> toBatchResponseList(List<Batch> batch);
}
