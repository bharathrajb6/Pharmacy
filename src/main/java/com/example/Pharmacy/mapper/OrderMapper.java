package com.example.Pharmacy.mapper;


import com.example.Pharmacy.dtos.responses.OrderResponse;
import com.example.Pharmacy.model.Orders;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toOrderResponse(Orders order);
}
