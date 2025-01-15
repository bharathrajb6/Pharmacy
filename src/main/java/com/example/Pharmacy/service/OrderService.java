package com.example.Pharmacy.service;

import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.dtos.responses.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request);

    OrderResponse getOrderDetails(String orderID);

    Page<OrderResponse> getAllOrdersByUsername(String username, Pageable pageable);

    Page<OrderResponse> getAllOrders(Pageable pageable);

    OrderResponse cancelOrder(String orderID);

    Page<OrderResponse> getAllCancelledOrder(Pageable pageable);

    Page<OrderResponse> getAllCancelledOrdersByUsername(String username, Pageable pageable);
}
