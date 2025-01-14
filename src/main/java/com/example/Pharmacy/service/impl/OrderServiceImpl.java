package com.example.Pharmacy.service.impl;

import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.dtos.responses.OrderResponse;
import com.example.Pharmacy.exception.OrderException;
import com.example.Pharmacy.helper.OrderServiceHelper;
import com.example.Pharmacy.mapper.OrderMapper;
import com.example.Pharmacy.model.MedicationQuantity;
import com.example.Pharmacy.model.Orders;
import com.example.Pharmacy.repo.MedicationQuantityRepository;
import com.example.Pharmacy.repo.OrderRepository;
import com.example.Pharmacy.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MedicationQuantityRepository medicationQuantityRepository;
    private final OrderMapper orderMapper;
    private final OrderServiceHelper orderServiceHelper;

    @Override
    public OrderResponse placeOrder(OrderRequest request) {
        Orders order = orderServiceHelper.generateOrder(request);
        List<MedicationQuantity> medicationQuantityList = orderServiceHelper.generateMedicationQuantity(request.getMedicationOrderRequestList(), order);
        try {
            orderRepository.save(order);
            medicationQuantityRepository.saveAll(medicationQuantityList);
        } catch (Exception exception) {
            throw new OrderException("Unable to place the order");
        }
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse getOrderDetails(String orderID) {
        Orders orders = orderRepository.findById(orderID).orElseThrow(() -> new OrderException("Order not found"));
        OrderResponse orderResponse = orderMapper.toOrderResponse(orders);
        orderResponse.setMedications(orderServiceHelper.getMedicationOrderResponse(orders.getMedicationQuantityList()));
        return orderResponse;
    }


}
