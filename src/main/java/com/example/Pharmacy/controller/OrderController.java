package com.example.Pharmacy.controller;

import com.example.Pharmacy.dtos.request.OrderRequest;
import com.example.Pharmacy.dtos.responses.OrderResponse;
import com.example.Pharmacy.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public OrderResponse placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }


    @RequestMapping(value = "/order/{orderID}", method = RequestMethod.GET)
    public OrderResponse getOrderDetails(@PathVariable String orderID) {
        return orderService.getOrderDetails(orderID);
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public Page<OrderResponse> getOrdersByUsername(@RequestParam("username") String username, Pageable pageable) {
        return orderService.getAllOrdersByUsername(username, pageable);
    }

    @RequestMapping(value = "/order/all", method = RequestMethod.GET)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @RequestMapping(value = "/order/{orderID}/cancel", method = RequestMethod.PUT)
    public OrderResponse cancelOrder(@PathVariable String orderID) {
        return orderService.cancelOrder(orderID);
    }

    @RequestMapping(value = "/order/{orderID}/track", method = RequestMethod.GET)
    public String trackOrder(@PathVariable String orderID) {
        return orderService.getOrderDetails(orderID).getOrderStatus();
    }
}
