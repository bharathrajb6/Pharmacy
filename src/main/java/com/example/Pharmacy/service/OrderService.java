package com.example.Pharmacy.service;

import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.dtos.responses.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request);

    OrderResponse getOrderDetails(String orderID);
}
